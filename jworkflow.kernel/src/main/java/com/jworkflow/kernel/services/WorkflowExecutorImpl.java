package com.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.ExecutionPointer;
import com.jworkflow.kernel.models.WorkflowDefinition;
import com.jworkflow.kernel.models.WorkflowInstance;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import sun.util.logging.resources.logging;

public class WorkflowExecutorImpl implements WorkflowExecutor {

    
    private final WorkflowRegistry registry;
    private final Logger logger;
    
    @Inject
    public WorkflowExecutorImpl(WorkflowRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public void execute(WorkflowInstance workflow, PersistenceProvider persistenceStore, WorkflowHost host) {
        
        Stream<ExecutionPointer> exePointers = workflow.getExecutionPointers().stream().filter(x -> x.isActive());
        WorkflowDefinition def = registry.getDefinition(workflow.getWorkflowDefintionId(), workflow.getVersion());
        
        if (def == null) {
            logger.log(Level.SEVERE, "Workflow not registred");
            return;
        }
        
        exePointers.forEach(pointer -> {
            //pointer.setActive(true);
        });
    }
    
}
