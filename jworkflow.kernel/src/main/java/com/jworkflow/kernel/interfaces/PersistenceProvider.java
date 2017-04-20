package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.models.*;
import java.util.Date;

public interface PersistenceProvider {
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
