package com.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowExecutorImpl implements WorkflowExecutor {

    
    private final WorkflowRegistry registry;
    private final PersistenceProvider persistenceStore;
    private final WorkflowHost host;
    private final Logger logger;
    private final Injector injector;
    
    @Inject
    public WorkflowExecutorImpl(WorkflowRegistry registry, PersistenceProvider persistenceStore, WorkflowHost host, Logger logger, Injector injector) {
        this.registry = registry;
        this.persistenceStore = persistenceStore;
        this.host = host;
        this.logger = logger;
        this.injector = injector;
    }
    
    @Override
    public boolean execute(String workflowId) {
        
        WorkflowInstance workflow = persistenceStore.getWorkflowInstance(workflowId);
        if (workflow.getStatus() != WorkflowStatus.RUNNABLE)
            return false;
        
        ExecutionPointer[] exePointers = workflow.getExecutionPointers().stream().filter(x -> x.active).toArray(ExecutionPointer[]::new);
        
        WorkflowDefinition def = registry.getDefinition(workflow.getWorkflowDefintionId(), workflow.getVersion());
        
        if (def == null) {
            logger.log(Level.SEVERE, "Workflow not registred");
            return false;
        }
                
        for (ExecutionPointer pointer: exePointers) {
            
            Optional<WorkflowStep> step = def.getSteps().stream().filter(x -> x.getId() == pointer.stepId).findFirst();
            
            if (step.isPresent()) {
                
                try {
                    
                    if (step.get().initForExecution(host, persistenceStore, def, workflow, pointer) == ExecutionPipelineResult.DEFER)
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
                    
                    if (step.get().beforeExecute(host, persistenceStore, context, pointer, body) == ExecutionPipelineResult.DEFER)
                        continue;
                    
                    ExecutionResult result = body.run(context);
                    
                    processOutputs(step.get(), body, workflow.getData());
                    processExecutionResult(result, pointer, step, workflow);
                            
                    step.get().afterExecute(host, persistenceStore, context, result, pointer);
                
                } catch (Exception ex) {
                    Logger.getLogger(WorkflowExecutorImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            else {
                // throw
            }
            persistenceStore.persistWorkflow(workflow);
        }
        
        determineNextExecution(workflow);
        persistenceStore.persistWorkflow(workflow);
        
        if (workflow.getNextExecution() == null)
            return false;
        
        long now = new Date().getTime();
        return ((workflow.getNextExecution() < now) && workflow.getStatus() == WorkflowStatus.RUNNABLE);
    }

    private void processExecutionResult(ExecutionResult result, ExecutionPointer pointer, Optional<WorkflowStep> step, WorkflowInstance workflow) {
        if (result.isProceed()) {
            pointer.active = false;
            pointer.endTimeUtc = Date.from(Instant.now());
            int forkCounter = 1;
            boolean noOutcome = true;
            
            StepOutcome[] outcomes = step.get().getOutcomes().stream().filter(x -> x.getValue() == result.getOutcomeValue()).toArray(StepOutcome[]::new);
            
            for (StepOutcome outcome : outcomes) {
                
                ExecutionPointer newPointer = new ExecutionPointer();
                newPointer.id = UUID.randomUUID().toString();
                newPointer.active = true;
                newPointer.stepId = outcome.getNextStep();
                newPointer.concurrentFork = (forkCounter * pointer.concurrentFork);
                workflow.getExecutionPointers().add(newPointer);
                noOutcome = false;
                forkCounter++;
            }
            
            pointer.pathTerminator = noOutcome;
            
            //pointer.
        }
        else {  //no proceed
            pointer.persistenceData = result.getPersistenceData();
            //todo: sleeps
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
        
        
        for(ExecutionPointer pointer : workflow.getExecutionPointers()) { 
            if (pointer.active) {
                if ((pointer.sleepUntilUtc == null)) {
                    workflow.setNextExecution((long)0);
                    return;
                }
                long pointerSleep = pointer.sleepUntilUtc.getTime();
                workflow.setNextExecution(Math.min(pointerSleep, workflow.getNextExecution() != null ? workflow.getNextExecution() : pointerSleep));
            }            
        }        
                
        if (workflow.getNextExecution() == null) {
            int forks = 1;
            int terminals = 0;
            
            for(ExecutionPointer pointer : workflow.getExecutionPointers()) { 
                forks = Math.max(pointer.concurrentFork, forks);
                if (pointer.pathTerminator)
                    terminals++;
                
                if (forks <= terminals) {
                    workflow.setStatus(WorkflowStatus.COMPLETE);
                    workflow.setCompleteTimeUtc(Date.from(Instant.now()));
                }                
             }          
        }
        
    }
    
}
