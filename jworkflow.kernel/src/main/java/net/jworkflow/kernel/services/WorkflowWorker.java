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

public class WorkflowWorker extends QueueWorker {
    
    private final WorkflowExecutor executor;
    private final PersistenceService persistenceStore;
    private final LockService lockProvider;
    
    @Inject
    public WorkflowWorker(WorkflowExecutor executor, PersistenceService persistence, QueueService queueProvider, LockService lockProvider, Logger logger) {
        super(queueProvider, logger);
        this.executor = executor;
        this.lockProvider = lockProvider;
        this.persistenceStore = persistence;
    }

    @Override
    protected QueueType getQueueType() {
        return QueueType.WORKFLOW;
    }

    @Override
    protected void executeItem(String item) throws Exception {
        if (!lockProvider.acquireLock(item)) {
            logger.log(Level.INFO, String.format("Workflow %s locked", item));
            return;
        }

        WorkflowExecutorResult result = new WorkflowExecutorResult();
        try {
            WorkflowInstance workflow = persistenceStore.getWorkflowInstance(item);
            try {
                result = executor.execute(workflow);                            
            }
            finally {
                persistenceStore.persistWorkflow(workflow);
            }                        
        }
        finally {
            lockProvider.releaseLock(item);

            for (EventSubscription evt: result.subscriptions) {
                subscribeEvent(evt);
            }

            if (result.requeue) {
                logger.log(Level.INFO, String.format("Requeue workflow %s", item));
                queueProvider.queueForProcessing(QueueType.WORKFLOW, item);
            }
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

    @Override
    protected int getThreadCount() {
        return Runtime.getRuntime().availableProcessors();
    }
    
}
