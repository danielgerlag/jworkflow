package net.jworkflow.kernel.services;

import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.interfaces.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultWorkflowExecutor implements WorkflowExecutor {
    private final WorkflowRegistry registry;
    private final ExecutionResultProcessor resultProcessor;
    private final Clock clock;
    private final Logger logger;
    private final Injector injector;
    
    @Inject
    public DefaultWorkflowExecutor(WorkflowRegistry registry, ExecutionResultProcessor resultProcessor, Clock clock, Logger logger, Injector injector) {
        this.registry = registry;
        this.resultProcessor = resultProcessor;
        this.logger = logger;
        this.clock = clock;
        this.injector = injector;
    }
    
    @Override
    public WorkflowExecutorResult execute(WorkflowInstance workflow) {
        WorkflowExecutorResult wfResult = new WorkflowExecutorResult();
        wfResult.requeue = false;
                
        if (workflow.getStatus() != WorkflowStatus.RUNNABLE)
            return wfResult;
        
        List<ExecutionPointer> exePointers = workflow.getExecutionPointers().findMany(x -> x.active);
        WorkflowDefinition def = registry.getDefinition(workflow.getWorkflowDefintionId(), workflow.getVersion());
        
        if (def == null) {
            logger.log(Level.SEVERE, "Workflow not registred");
            return wfResult;
        }
                
        for (ExecutionPointer pointer: exePointers) {
            
            Optional<WorkflowStep> step = def.getSteps().stream().filter(x -> x.getId() == pointer.stepId).findFirst();
                        
            if (!step.isPresent()) {
                logger.log(Level.SEVERE, "Step not found in definition");
                continue;
            }            
            
            try {
                if (step.get().initForExecution(wfResult, def, workflow, pointer) == ExecutionPipelineResult.DEFER)
                    continue;

                if (pointer.startTimeUtc == null)
                    pointer.startTimeUtc = Date.from(Instant.now(clock));

                logger.log(Level.INFO, String.format("Starting step %s on workflow %s", step.get().getName(), workflow.getId()));

                StepBody body = (StepBody)step.get().constructBody(injector);

                //todo: inputs
                processInputs(step.get(), body, workflow.getData());

                StepExecutionContext context = new StepExecutionContext();
                context.setWorkflow(workflow);
                context.setStep(step.get());
                context.setPersistenceData(pointer.persistenceData);
                context.setItem(pointer.contextItem);
                context.setExecutionPointer(pointer);

                if (step.get().beforeExecute(wfResult, context, pointer, body) == ExecutionPipelineResult.DEFER)
                    continue;

                ExecutionResult result = body.run(context);

                if (result.isProceed()) {
                    processOutputs(step.get(), body, workflow.getData());
                }                    

                resultProcessor.processExecutionResult(workflow, def, pointer, step.get(), result, wfResult);

                step.get().afterExecute(wfResult, context, result, pointer);

            } catch (Exception ex) {
                Logger.getLogger(DefaultWorkflowExecutor.class.getName()).log(Level.SEVERE, null, ex);
                resultProcessor.handleStepException(workflow, def, pointer, step.get());
            }                 
        }
        processAfterExecutionIteration(workflow, def, wfResult);
        determineNextExecution(workflow);
                
        if (workflow.getNextExecution() == null)
            return wfResult;
        
        long now = new Date().getTime();
        wfResult.requeue = ((workflow.getNextExecution() < now) && workflow.getStatus() == WorkflowStatus.RUNNABLE);
        
        return wfResult;
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
                if ((pointer.sleepUntil == null) ) {
                    workflow.setNextExecution((long)0);
                    return;
                }
                
                long pointerSleep = pointer.sleepUntil.toInstant().toEpochMilli();
                workflow.setNextExecution(Math.min(pointerSleep, workflow.getNextExecution() != null ? workflow.getNextExecution() : pointerSleep));
            }            
        }        
        
        if (workflow.getNextExecution() == null) {
            for (ExecutionPointer pointer : workflow.getExecutionPointers()) { 
                if ((pointer.active) && (!pointer.children.isEmpty())) {
                    if (workflow.getExecutionPointers().streamMany(x -> pointer.children.contains(x.id)).allMatch(x -> workflow.isBranchComplete(x.id))) {
                        workflow.setNextExecution((long)0);
                        return;
                    }
                }
            }
        }
        
        if (workflow.getNextExecution() == null) {            
            if (workflow.getExecutionPointers().stream().allMatch(x -> x.endTimeUtc != null)) {
                workflow.setStatus(WorkflowStatus.COMPLETE);
                workflow.setCompleteTimeUtc(Date.from(Instant.now(clock)));
            }                      
        }        
    }

    private void processAfterExecutionIteration(WorkflowInstance workflow, WorkflowDefinition workflowDef, WorkflowExecutorResult workflowResult) {
        List<ExecutionPointer> pointers = workflow.getExecutionPointers().findMany(x -> x.endTimeUtc == null);

        pointers.forEach((pointer) -> {
            WorkflowStep step = workflowDef.findStep(pointer.stepId);
            step.afterWorkflowIteration(workflowResult, workflowDef, workflow, pointer);
        });
    }    
}
