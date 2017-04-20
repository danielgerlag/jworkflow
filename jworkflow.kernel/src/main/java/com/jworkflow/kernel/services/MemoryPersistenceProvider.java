package com.jworkflow.kernel.services;
import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class MemoryPersistenceProvider implements PersistenceProvider {
    
    
    private final List<WorkflowInstance> workflows;
    private final List<Event> events;
    private final List<EventSubscription> subscriptions;
    
    public MemoryPersistenceProvider() {
        workflows = new ArrayList<>();
        events = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    @Override
    public synchronized String createNewWorkflow(WorkflowInstance workflow) {        
        workflow.setId(UUID.randomUUID().toString());
        workflows.add(workflow);
        return workflow.getId();        
    }

    @Override
    public synchronized void persistWorkflow(WorkflowInstance workflow) {        
        workflows.removeIf(x -> (x.getId() == null ? workflow.getId() == null : x.getId().equals(workflow.getId())));
        workflows.add(workflow);
    }

    @Override
    public synchronized Iterable<String> getRunnableInstances() {
        ArrayList<String> result = new ArrayList<>();
        workflows.stream().filter(x -> x.getStatus() == WorkflowStatus.RUNNABLE).forEach(item -> {
            result.add(item.getId());
        });        
        return result;
    }

    @Override
    public synchronized WorkflowInstance getWorkflowInstance(String id) {
        Optional<WorkflowInstance> result = workflows.stream().filter(x -> (x.getId() == null ? id == null : x.getId().equals(id))).findFirst();
        if (result.isPresent())
            return result.get();
        else
            return null;        
    }

    @Override
    public String createEventSubscription(EventSubscription subscription) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<EventSubscription> getSubcriptions(String eventName, String eventKey, Date asOf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void terminateSubscription(String eventSubscriptionId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized String createEvent(Event newEvent) {
        newEvent.id = UUID.randomUUID().toString();
        events.add(newEvent);
        return newEvent.id;
    }

    @Override
    public synchronized Event getEvent(String id) {
        Optional<Event> result = events.stream().filter(x -> (x.id == null ? id == null : x.id.equals(id))).findFirst();
        if (result.isPresent())
            return result.get();
        else
            return null;
    }

    @Override
    public Iterable<String> getRunnableEvents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<String> getEvents(String eventName, String eventKey, Date asOf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void markEventProcessed(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void markEventUnprocessed(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
