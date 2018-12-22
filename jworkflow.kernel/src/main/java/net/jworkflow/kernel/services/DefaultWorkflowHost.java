package net.jworkflow.kernel.services;

import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.QueueType;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowStatus;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.Event;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.interfaces.*;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class DefaultWorkflowHost implements WorkflowHost {
    
    private boolean active;
    private final PersistenceService persistenceProvider;
    private final QueueService queueProvider;
    private final LockService lockProvider;
    private final WorkflowRegistry registry;
    private final ExecutionPointerFactory pointerFactory;
    private final List<ScheduledFuture> workerFutures;
    private final ScheduledExecutorService scheduler;
    private final Clock clock;
    private final Injector injector;
    private final Logger logger;
    
    private ScheduledFuture pollFuture;
    
    @Inject
    public DefaultWorkflowHost(PersistenceService persistenceProvider, QueueService queueProvider, LockService lockProvider, WorkflowRegistry registry, ExecutionPointerFactory pointerFactory, Clock clock, Injector injector, Logger logger) {
        
        Runtime runtime = Runtime.getRuntime();
        
        this.persistenceProvider = persistenceProvider;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;
        this.registry = registry;        
        this.pointerFactory = pointerFactory;
        this.clock = clock;
        this.injector = injector;
        this.logger = logger;
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
        wf.setCreateTimeUtc(Date.from(Instant.now(clock)));
        wf.setStatus(WorkflowStatus.RUNNABLE);
        
        if ((def.getDataType() != null) && (data == null)) {
            wf.setData(def.getDataType().newInstance());
        }

        wf.getExecutionPointers().add(pointerFactory.buildGenesisPointer(def));
        String id = persistenceProvider.createNewWorkflow(wf);
        
        queueProvider.queueForProcessing(QueueType.WORKFLOW, id);
        
        return id;
    }

    @Override
    public void start() {
        active = true;
        lockProvider.start();
        
        WorkflowThread wfWorker = injector.getInstance(WorkflowThread.class);            
        workerFutures.add(scheduler.scheduleAtFixedRate(wfWorker, 100, 100, TimeUnit.MILLISECONDS));
        
        EventThread evtWorker = injector.getInstance(EventThread.class);            
        workerFutures.add(scheduler.scheduleAtFixedRate(evtWorker, 100, 100, TimeUnit.MILLISECONDS));
        
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
        lockProvider.stop();
    }

    @Override
    public void registerWorkflow(Class<? extends Workflow> workflow) throws Exception {
        Workflow wf = workflow.newInstance();
        registry.registerWorkflow(wf);
    }
    
    @Override
    public void registerWorkflow(Workflow workflow) throws Exception {        
        registry.registerWorkflow(workflow);
    }        
    
    @Override
    public void publishEvent(String eventName, String eventKey, Object eventData, Date effectiveDateUtc) throws Exception {
        if (!active)
            throw new Exception("Host is not running");

        logger.log(Level.INFO, String.format("Creating event %s %s", eventName, eventKey));
        
        Event evt = new Event();

        //TODO: use utc
        if (effectiveDateUtc != null)
            evt.eventTimeUtc = effectiveDateUtc;
        else
            evt.eventTimeUtc = Date.from(Instant.now(clock));

        evt.eventData = eventData;
        evt.eventKey = eventKey;
        evt.eventName = eventName;
        evt.isProcessed = false;
        String eventId = persistenceProvider.createEvent(evt);

        queueProvider.queueForProcessing(QueueType.EVENT, eventId);
    }
    
    @Override
    public boolean suspendWorkflow(String workflowId) {
        if (lockProvider.acquireLock(workflowId)) {
            try {
                WorkflowInstance wf = persistenceProvider.getWorkflowInstance(workflowId);
                if (wf.getStatus() == WorkflowStatus.RUNNABLE) {
                    wf.setStatus(WorkflowStatus.SUSPENDED);
                    persistenceProvider.persistWorkflow(wf);
                    return true;
                }
                return false;
            }
            finally {
                lockProvider.releaseLock(workflowId);
            }
        }
        return false;
    }
    
    @Override
    public boolean resumeWorkflow(String workflowId) {
        if (lockProvider.acquireLock(workflowId)) {
            boolean requeue = false;
            try {
                WorkflowInstance wf = persistenceProvider.getWorkflowInstance(workflowId);
                if (wf.getStatus() == WorkflowStatus.SUSPENDED) {
                    wf.setStatus(WorkflowStatus.RUNNABLE);
                    persistenceProvider.persistWorkflow(wf);
                    requeue = true;
                    return true;
                }
                return false;
            }
            finally {
                lockProvider.releaseLock(workflowId);
                if (requeue)
                    queueProvider.queueForProcessing(QueueType.WORKFLOW, workflowId);
            }
        }
        return false;
    }

}
