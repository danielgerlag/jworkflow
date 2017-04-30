package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.Event;
import net.jworkflow.kernel.models.WorkflowInstance;
import java.util.Date;

public interface PersistenceService {
    String createNewWorkflow(WorkflowInstance workflow);
    void persistWorkflow(WorkflowInstance workflow);
    Iterable<String> getRunnableInstances();
    WorkflowInstance getWorkflowInstance(String id);    
    String createEventSubscription(EventSubscription subscription);
    Iterable<EventSubscription> getSubcriptions(String eventName, String eventKey, Date asOf);
    void terminateSubscription(String eventSubscriptionId);
    String createEvent(Event newEvent);
    Event getEvent(String id);
    Iterable<String> getRunnableEvents();
    Iterable<String> getEvents(String eventName, String eventKey, Date asOf);
    void markEventProcessed(String id);
    void markEventUnprocessed(String id);
}
