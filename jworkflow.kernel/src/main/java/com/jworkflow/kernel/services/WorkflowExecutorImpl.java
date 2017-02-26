package com.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import sun.util.logging.resources.logging;

public class WorkflowExecutorImpl implements WorkflowExecutor {

    
    private final WorkflowRegistry registry;
    private final Logger logger;
    
    @Inject
    public WorkflowExecutorImpl(WorkflowRegistry registry, Logger logger) {
        this.registry = registry;
        this.logger = logger;
    }
    
    @Override
    public void execute(WorkflowInstance workflow, PersistenceProvider persistenceStore, WorkflowHost host) {
        
        Stream<ExecutionPointer> exePointers = workflow.getExecutionPointers().stream().filter(x -> x.isActive());
        WorkflowDefinition def = registry.getDefinition(workflow.getWorkflowDefintionId(), workflow.getVersion());
        
        if (def == null) {
            logger.log(Level.SEVERE, "Workflow not registred");
            return;
        }
        
        exePointers.forEach((ExecutionPointer pointer) -> {
            
            Optional<WorkflowStep> step = def.getSteps().stream().filter(x -> x.getId() == pointer.getStepId()).findFirst();
            
            if (step.isPresent()) {
                
                try {
                    if (pointer.getStartTime() == null)
                        pointer.setStartTime(new Date());
                    
                    logger.log(Level.INFO, String.format("Starting step %s on workflow %s", step.get().getName(), workflow.getId()));
                    
                    StepBody body = (StepBody)step.get().constructBody();
                    
                    //todo: inputs
                    
                    StepExecutionContext context = new StepExecutionContext();
                    context.setWorkflow(workflow);
                    context.setStep(step.get());
                    context.setPersistenceData(pointer.getPersistenceData());
                    
                    ExecutionResult result = body.run(context);
                    
                    //todo: outputs
                    
                    if (result.isProceed()) {
                        pointer.setActive(false);
                        pointer.setEndTime(new Date());
                        int forkCounter = 1;
                        boolean noOutcome = true;
                        
                        StepOutcome[] outcomes = (StepOutcome[])step.get().getOutcomes().stream().filter(x -> x.getValue() == result.getOutcomeValue()).toArray();
                        
                        for (StepOutcome outcome : outcomes) {
                            
                            ExecutionPointer newPointer = new ExecutionPointer();
                            newPointer.setActive(true);
                            newPointer.setStepId(outcome.getNextStep());
                            newPointer.setConcurrentFork(forkCounter * pointer.getConcurrentFork());
                            workflow.getExecutionPointers().add(newPointer);
                            noOutcome = false;
                            forkCounter++;
                        }
                                
                        pointer.setPathTerminator(noOutcome);
                                                
                        //pointer.
                    }
                    else {  //no proceed
                        pointer.setPersistenceData(result.getPersistenceData());
                        //todo: sleeps
                    }
                            
                    
                
                } catch (Exception ex) {
                    Logger.getLogger(WorkflowExecutorImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
            else {
                // throw
            }
            persistenceStore.persistWorkflow(workflow);
        });
        
        determineNextExecution(workflow);
        persistenceStore.persistWorkflow(workflow);
    }
    
    private void determineNextExecution(WorkflowInstance workflow) {
        workflow.setNextExecution(null);
        
        workflow.getExecutionPointers().stream().filter(x -> x.isActive()).forEach(pointer -> {
            if (pointer.getSleepUntil() != null) {
                workflow.setNextExecution((long)0);
                return;
            }
            long pointerSleep = pointer.getSleepUntil().getTime();
            workflow.setNextExecution(Math.min(pointerSleep, workflow.getNextExecution() != null ? workflow.getNextExecution() : pointerSleep));
        });
        
        if (workflow.getNextExecution() == null) {
            int forks = 1;
            int terminals = 0;
            
            for(ExecutionPointer pointer : workflow.getExecutionPointers()) { 
                forks = Math.max(pointer.getConcurrentFork(), forks);
                if (pointer.isPathTerminator())
                    terminals++;
                
                if (forks <= terminals) {
                    workflow.setStatus(WorkflowStatus.COMPLETE);
                    workflow.setCompleteTime(new Date());
                }
                
             }
            
                        
        }
        
    }
    
}
