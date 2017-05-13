package net.jworkflow.kernel.services;

import net.jworkflow.kernel.models.StepOutcome;
import net.jworkflow.kernel.models.WorkflowStep;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowStatus;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.ExecutionPipelineResult;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.StepExecutionContext;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.interfaces.WorkflowRegistry;
import net.jworkflow.kernel.interfaces.WorkflowExecutor;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.interfaces.PersistenceService;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.models.WorkflowExecutorResult;

public class WorkflowExecutorImpl implements WorkflowExecutor {

    
    private final WorkflowRegistry registry;
    private final Logger logger;
    private final Injector injector;
    
    @Inject
    public WorkflowExecutorImpl(WorkflowRegistry registry, Logger logger, Injector injector) {
        this.registry = registry;
        this.logger = logger;
        this.injector = injector;
    }
    
    @Override
    public WorkflowExecutorResult execute(WorkflowInstance workflow) {
        WorkflowExecutorResult wfResult = new WorkflowExecutorResult();
        wfResult.requeue = false;
                
        if (workflow.getStatus() != WorkflowStatus.RUNNABLE)
            return wfResult;
        
        ExecutionPointer[] exePointers = workflow.getExecutionPointers().stream().filter(x -> x.active).toArray(ExecutionPointer[]::new);
        
        WorkflowDefinition def = registry.getDefinition(workflow.getWorkflowDefintionId(), workflow.getVersion());
        
        if (def == null) {
            logger.log(Level.SEVERE, "Workflow not registred");
            return wfResult;
        }
                
        for (ExecutionPointer pointer: exePointers) {
            
            Optional<WorkflowStep> step = def.getSteps().stream().filter(x -> x.getId() == pointer.stepId).findFirst();
            
            if (step.isPresent()) {
                
                try {
                    
                    if (step.get().initForExecution(wfResult, def, workflow, pointer) == ExecutionPipelineResult.DEFER)
                        continue;
                    
                    if (pointer.startTimeUtc == null)
                        pointer.startTimeUtc = Date.from(Instant.now());
                    
                    logger.log(Level.INFO, String.format("Starting step %s on workflow %s", step.get().getName(), workflow.getId()));
                    
                    StepBody body = (StepBody)step.get().constructBody(injector);
                    
                    //todo: inputs
                    processInputs(step.get(), body, workflow.getData());
                                        
                    StepExecutionContext context = new StepExecutionContext();
                    context.setWorkflow(workflow);
                    context.setStep(step.get());
                    context.setPersistenceData(pointer.persistenceData);
                    
                    if (step.get().beforeExecute(wfResult, context, pointer, body) == ExecutionPipelineResult.DEFER)
                        continue;
                    
                    ExecutionResult result = body.run(context);
                    
                    processOutputs(step.get(), body, workflow.getData());
                    processExecutionResult(result, pointer, step, workflow);
                            
                    step.get().afterExecute(wfResult, context, result, pointer);
                
                } catch (Exception ex) {
                    Logger.getLogger(WorkflowExecutorImpl.class.getName()).log(Level.SEVERE, null, ex);
                    
                    switch (step.get().getRetryBehavior()) {
                        case RETRY:
                            pointer.sleepFor = step.get().getRetryInterval();
                            pointer.retryCounter++;
                            break;
                        case SUSPEND:
                            workflow.setStatus(WorkflowStatus.SUSPENDED);
                            break;
                        case TERMINATE:
                            workflow.setStatus(WorkflowStatus.TERMINATED);
                            break;
                    }                    
                }
            }
            else {
                logger.log(Level.SEVERE, "Step not found in definition");
            }            
        }
        
        determineNextExecution(workflow);
                
        if (workflow.getNextExecution() == null)
            return wfResult;
        
        long now = new Date().getTime();
        wfResult.requeue = ((workflow.getNextExecution() < now) && workflow.getStatus() == WorkflowStatus.RUNNABLE);
        
        return wfResult;
    }

    private void processExecutionResult(ExecutionResult result, ExecutionPointer pointer, Optional<WorkflowStep> step, WorkflowInstance workflow) {
        //TODO: move to own class
        
        pointer.persistenceData = result.getPersistenceData();            
        pointer.sleepFor = result.getSleepFor();
        
        if (result.isProceed()) {
            pointer.active = false;
            pointer.sleepFor = null;
            pointer.endTimeUtc = Date.from(Instant.now());
            
            StepOutcome[] outcomes = step.get().getOutcomes().stream().filter(x -> x.getValue() == result.getOutcomeValue()).toArray(StepOutcome[]::new);
            
            for (StepOutcome outcome : outcomes) {
                
                ExecutionPointer newPointer = new ExecutionPointer();
                newPointer.id = UUID.randomUUID().toString();
                newPointer.active = true;
                newPointer.predecessorId = pointer.id;
                newPointer.contextItem = pointer.contextItem;
                newPointer.stepId = outcome.getNextStep();
                workflow.getExecutionPointers().add(newPointer);
            }
        }
        else {  //no proceed
            for (Object branchValue: result.getBranches()) {
                for (int childId: step.get().getChildren()) {                                        
                    ExecutionPointer newPointer = new ExecutionPointer();
                    newPointer.id = UUID.randomUUID().toString();
                    newPointer.active = true;
                    newPointer.predecessorId = pointer.id;
                    newPointer.contextItem = branchValue;
                    newPointer.stepId = childId;
                    workflow.getExecutionPointers().add(newPointer);
                    
                    pointer.children.add(newPointer.id);
                }
            }
        }
    }
    
    private void processInputs(WorkflowStep step, StepBody body, Object data) {        
        step.getInputs().stream().forEach((input) -> {            
            input.accept(body, data);
        });
        
    }
    
    private void processOutputs(WorkflowStep step, StepBody body, Object data) {        
        step.getOutputs().stream().forEach((input) -> {            
            input.accept(body, data);
        });
        
    }
    
    private void determineNextExecution(WorkflowInstance workflow) {
        workflow.setNextExecution(null);
                
        for (ExecutionPointer pointer : workflow.getExecutionPointers()) { 
            if ((pointer.active) && (pointer.children.isEmpty())) {
                if ((pointer.sleepFor == null) ) {
                    workflow.setNextExecution((long)0);
                    return;
                }
                
                long pointerSleep = Instant.now().plus(pointer.sleepFor).toEpochMilli();
                workflow.setNextExecution(Math.min(pointerSleep, workflow.getNextExecution() != null ? workflow.getNextExecution() : pointerSleep));
            }            
        }        
        
        if (workflow.getNextExecution() == null) {
            for (ExecutionPointer pointer : workflow.getExecutionPointers()) { 
                if ((pointer.active) && (!pointer.children.isEmpty())) {
                    if (workflow.getExecutionPointers().stream().filter(x -> pointer.children.contains(x.id)).allMatch(x -> x.endTimeUtc != null)) {
                        workflow.setNextExecution((long)0);
                        return;
                    }
                }
            }
        }
        
        if (workflow.getNextExecution() == null) {            
            if (workflow.getExecutionPointers().stream().allMatch(x -> x.endTimeUtc != null)) {
                workflow.setStatus(WorkflowStatus.COMPLETE);
                workflow.setCompleteTimeUtc(Date.from(Instant.now()));
            }                      
        }        
    }    
}
