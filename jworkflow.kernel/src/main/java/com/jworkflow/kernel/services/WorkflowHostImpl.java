package com.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.*;
import java.util.UUID;
import com.jworkflow.kernel.models.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Singleton
public class WorkflowHostImpl implements WorkflowHost {
    
    private boolean active;
    private final PersistenceProvider persistenceProvider;
    private final QueueProvider queueProvider;
    private final LockProvider lockProvider;
    private final WorkflowRegistry registry;
    private final List<ScheduledFuture> workerFutures;
    private final ScheduledExecutorService scheduler;
    private final Injector injector;
    
    private ScheduledFuture pollFuture;
    
    @Inject
    public WorkflowHostImpl(PersistenceProvider persistenceProvider, QueueProvider queueProvider, LockProvider lockProvider, WorkflowRegistry registry, Injector injector) {
        
        Runtime runtime = Runtime.getRuntime();
        
        this.persistenceProvider = persistenceProvider;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;
        this.registry = registry;        
        this.injector = injector;
        this.scheduler = Executors.newScheduledThreadPool(runtime.availableProcessors());
        active = false;
        workerFutures = new ArrayList<>();        
    }
    

    @Override
    public String startWorkflow(String workflowId, int version, Object data) throws Exception {
        
        if (!active)
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
        
        queueProvider.queueForProcessing(id);
        
        return id;
    }

    @Override
    public void start() {
        active = true;
        WorkflowThread worker = injector.getInstance(WorkflowThread.class);            
        workerFutures.add(scheduler.scheduleAtFixedRate(worker, 100, 100, TimeUnit.MILLISECONDS));
        PollThread poller = injector.getInstance(PollThread.class);
        pollFuture = scheduler.scheduleAtFixedRate(poller, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        active = false;
        pollFuture.cancel(true);
        workerFutures.forEach((worker) -> {
            worker.cancel(false);
        });
        workerFutures.clear();
    }

    @Override
    public void registerWorkflow(Workflow workflow) throws Exception {
        registry.registerWorkflow(workflow);
    }
    
}
