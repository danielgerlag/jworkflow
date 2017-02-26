package com.jworkflow.kernel.services;

import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.*;
import java.util.UUID;
import com.jworkflow.kernel.models.*;
import java.util.Date;

@Singleton
public class WorkflowHostImpl implements WorkflowHost {
    
    private boolean shutdown;
    private final PersistenceProvider persistenceProvider;
    private final QueueProvider queueProvider;
    private final LockProvider lockProvider;
    private final WorkflowRegistry registry;
    
    public WorkflowHostImpl(PersistenceProvider persistenceProvider, QueueProvider queueProvider, LockProvider lockProvider, WorkflowRegistry registry) {
        this.persistenceProvider = persistenceProvider;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;
        this.registry = registry;
        shutdown = true;
    }
    

    @Override
    public String startWorkflow(String workflowId, int version, Object data) throws Exception {
        
        if (shutdown)
            throw new Exception("Host is not running");
        
        WorkflowDefinition def = registry.getDefinition(workflowId, version);
        
        if (def == null)
            throw new Exception(String.format("Workflow %s version %s is not registered", workflowId, version));
        
        WorkflowInstance wf = new WorkflowInstance();        
        wf.setWorkflowDefintionId(workflowId);
        wf.setVersion(version);
        wf.setData(data);
        wf.setDescription(def.getDescription());
        wf.setNextExecution((long)0);
        wf.setCreateTime(new Date());
        wf.setStatus(WorkflowStatus.RUNNABLE);
        
        if ((def.getDataType() != null) && (data == null)) {
            wf.setData(def.getDataType().newInstance());
        }

        ExecutionPointer ep = new ExecutionPointer();
        ep.setId(UUID.randomUUID().toString());
        ep.setActive(true);
        ep.setStepId(def.getInitialStep());
        ep.setConcurrentFork(1);
        
        wf.getExecutionPointers().add(ep);
        String id = persistenceProvider.createNewWorkflow(wf);
        
        return id;
    }
    
}
