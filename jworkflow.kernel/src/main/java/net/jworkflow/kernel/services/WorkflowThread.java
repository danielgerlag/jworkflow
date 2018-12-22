package net.jworkflow.kernel.services;

import net.jworkflow.kernel.interfaces.WorkflowExecutor;
import net.jworkflow.kernel.interfaces.LockService;
import net.jworkflow.kernel.interfaces.QueueService;
import com.google.inject.Inject;
import net.jworkflow.kernel.models.QueueType;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.PersistenceService;
import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.WorkflowExecutorResult;
import net.jworkflow.kernel.models.WorkflowInstance;

public class WorkflowThread implements Runnable {
    
    private final WorkflowExecutor executor;
    private final PersistenceService persistenceStore;
    private final QueueService queueProvider;
    private final LockService lockProvider;
    private final Logger logger;
    
    @Inject
    public WorkflowThread(WorkflowExecutor executor, PersistenceService persistence, QueueService queueProvider, LockService lockProvider, Logger logger) {
        this.executor = executor;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;        
        this.logger = logger;        
        this.persistenceStore = persistence;
    }

    @Override
    public void run() {
        try {
            String workflowId = queueProvider.dequeueForProcessing(QueueType.WORKFLOW);
            
            if (workflowId == null) {
                if (!queueProvider.isDequeueBlocking()) {
                    Thread.sleep(1000);
                }
                return;
            }            
            
            if (!lockProvider.acquireLock(workflowId)) {
                logger.log(Level.INFO, String.format("Workflow %s locked", workflowId));
                return;
            }

            WorkflowExecutorResult result = new WorkflowExecutorResult();
            try {
                WorkflowInstance workflow = persistenceStore.getWorkflowInstance(workflowId);
                try {
                    result = executor.execute(workflow);                            
                }
                finally {
                    persistenceStore.persistWorkflow(workflow);
                }                        
            }
            finally {
                lockProvider.releaseLock(workflowId);

                for (EventSubscription evt: result.subscriptions) {
                    subscribeEvent(evt);
                }

                if (result.requeue) {
                    logger.log(Level.INFO, String.format("Requeue workflow %s", workflowId));
                    queueProvider.queueForProcessing(QueueType.WORKFLOW, workflowId);
                }
            }            
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString());
        }
    }
    
    private void subscribeEvent(EventSubscription subscription) {
        //TODO: move to own class
        logger.log(Level.INFO, String.format("Subscribing to event {%s} {%s} for workflow {%s} step {%s}", subscription.eventName, subscription.eventKey, subscription.workflowId, subscription.stepId));

        persistenceStore.createEventSubscription(subscription);
        
        Iterable<String> events = persistenceStore.getEvents(subscription.eventName, subscription.eventKey, subscription.subscribeAsOfUtc);
        for (String evt: events) {
            persistenceStore.markEventUnprocessed(evt);
            queueProvider.queueForProcessing(QueueType.EVENT, evt);
        }
    }
    
}
