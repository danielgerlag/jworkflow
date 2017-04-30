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

public class EventThread implements Runnable {
        
    private final PersistenceService persistenceProvider;
    private final QueueService queueProvider;
    private final LockService lockProvider;
    private final Logger logger;
    
    @Inject
    public EventThread(PersistenceService persistenceProvider, QueueService queueProvider, LockService lockProvider, Logger logger) {
        this.persistenceProvider = persistenceProvider;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;        
        this.logger = logger;        
    }

    @Override
    public void run() {
        try {
            String eventId = queueProvider.dequeueForProcessing(QueueType.EVENT);
            if (eventId != null) {
                if (lockProvider.acquireLock("evt:" + eventId)) {
                    
                    try {
                        Event evt = persistenceProvider.getEvent(eventId);
                        if (evt.eventTimeUtc.before(new Date())) {
                            Iterable<EventSubscription> subs = persistenceProvider.getSubcriptions(evt.eventName, evt.eventKey, evt.eventTimeUtc);
                            boolean success = true;

                            for (EventSubscription sub : subs)
                                success = success && seedSubscription(evt, sub);

                            if (success)
                                persistenceProvider.markEventProcessed(eventId);
                        }
                    }
                    finally {
                        lockProvider.releaseLock("evt:" + eventId);                        
                    }
                }
                else {
                    logger.log(Level.INFO, String.format("Event %s locked", eventId));
                }
            }
        }
        catch (Exception ex) {

            logger.log(Level.SEVERE, ex.toString());
        }
    }
    
    private boolean seedSubscription(Event evt, EventSubscription sub) {
        if (lockProvider.acquireLock(sub.workflowId))
        {
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
            catch (Exception ex)
            {
                logger.log(Level.SEVERE, ex.toString());
                return false;
            }
            finally
            {
                lockProvider.releaseLock(sub.workflowId);
                queueProvider.queueForProcessing(QueueType.WORKFLOW, sub.workflowId);
            }
        }
        else
        {
            logger.log(Level.FINE, "Workflow locked {0}", sub.workflowId);
            return false;
        }
    }
    
}
