package com.jworkflow.kernel.services;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
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
    private final WorkflowRegistry registry;
    private final List<WorkerThread> threadPool;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    //private final Provider<WorkflowThread> workflowThreadProvider;
    //private final Provider<PollThread> pollThreadProvider;
    private final Injector injector;
    
    private ScheduledFuture pollFuture;
    
    @Inject
    public WorkflowHostImpl(PersistenceProvider persistenceProvider, WorkflowRegistry registry, Injector injector) {
        this.persistenceProvider = persistenceProvider;        
        this.registry = registry;
        //this.workflowThreadProvider = workflowThreadProvider;
        //this.pollThreadProvider = pollThreadProvider;
        this.injector = injector;
        active = false;
        threadPool = new ArrayList<>();        
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
        
        return id;
    }

    @Override
    public void start() {
        Runtime runtime = Runtime.getRuntime();
        
        active = true;
        
        for (int i = 0; i < runtime.availableProcessors(); i++) {
            WorkflowThread worker = injector.getInstance(WorkflowThread.class);  //workflowThreadProvider.get();
            Thread thread = new Thread(worker);
            threadPool.add(worker);
            thread.start();
        }
        PollThread poller = injector.getInstance(PollThread.class); //pollThreadProvider.get();
        pollFuture = scheduler.scheduleAtFixedRate(poller, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        active = false;
        pollFuture.cancel(true);
        threadPool.forEach((worker) -> {
            worker.setActive(false);
        });
        threadPool.clear();
    }
    
}
