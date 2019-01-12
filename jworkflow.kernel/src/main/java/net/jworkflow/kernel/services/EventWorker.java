package net.jworkflow.kernel.services;

import net.jworkflow.kernel.interfaces.LockService;
import net.jworkflow.kernel.interfaces.QueueService;
import net.jworkflow.kernel.interfaces.PersistenceService;
import com.google.inject.Inject;
import net.jworkflow.kernel.models.Event;
import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.QueueType;
import net.jworkflow.kernel.models.WorkflowInstance;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventWorker extends QueueWorker {
        
    private final PersistenceService persistenceProvider;
    private final LockService lockProvider;
    
    @Inject
    public EventWorker(PersistenceService persistenceProvider, QueueService queueProvider, LockService lockProvider, Logger logger) {
        super(queueProvider, logger);
        this.persistenceProvider = persistenceProvider;
        this.lockProvider = lockProvider;        
    }
    
    @Override
    protected QueueType getQueueType() {
        return QueueType.EVENT;
    }

    @Override
    protected void executeItem(String item) throws Exception {
        if (!lockProvider.acquireLock("evt:" + item)) {
            logger.log(Level.INFO, String.format("Event %s locked", item));
            return;
        }

        try {
            Event evt = persistenceProvider.getEvent(item);
            if (evt.eventTimeUtc.before(new Date())) {
                Iterable<EventSubscription> subs = persistenceProvider.getSubcriptions(evt.eventName, evt.eventKey, evt.eventTimeUtc);
                boolean success = true;

                for (EventSubscription sub : subs)
                    success = success && seedSubscription(evt, sub);

                if (success)
                    persistenceProvider.markEventProcessed(item);
            }
        }
        finally {
            lockProvider.releaseLock("evt:" + item);                        
        }
    }
    
    private boolean seedSubscription(Event evt, EventSubscription sub) {
        if (!lockProvider.acquireLock(sub.workflowId)) {
            logger.log(Level.FINE, "Workflow locked {0}", sub.workflowId);
            return false;
        }
                
        try
        {
            WorkflowInstance workflow = persistenceProvider.getWorkflowInstance(sub.workflowId);
            ExecutionPointer[] pointers = workflow.getExecutionPointers().stream()
                    .filter(p -> p.eventName != null && p.eventKey != null && !p.eventPublished)
                    .filter(p -> p.eventName.equals(sub.eventName) && p.eventKey.equals(sub.eventKey))
                    .toArray(ExecutionPointer[]::new);

            for (ExecutionPointer p: pointers) {
                p.eventData = evt.eventData;
                p.eventPublished = true;
                p.active = true;
            }

            workflow.setNextExecution((long)0);
            persistenceProvider.persistWorkflow(workflow);
            persistenceProvider.terminateSubscription(sub.id);
            return true;
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString());
            return false;
        }
        finally {
            lockProvider.releaseLock(sub.workflowId);
            queueProvider.queueForProcessing(QueueType.WORKFLOW, sub.workflowId);
        }        
    }

    @Override
    protected int getThreadCount() {
        return 1;
    }
}
