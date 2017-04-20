package com.jworkflow.providers.mongodb;

import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.models.Event;
import com.jworkflow.kernel.models.EventSubscription;
import com.jworkflow.kernel.models.WorkflowInstance;
import com.jworkflow.kernel.models.WorkflowStatus;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class MongoPersistenceProvider implements PersistenceProvider {

    MongoOperations mongoOperation;
    
    //@Inject
    //public MongoPersistenceProvider(String uri, String db) {
    public MongoPersistenceProvider() {        
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
        mongoOperation = (MongoOperations)ctx.getBean("mongoTemplate");        
    }
    
    
    @Override
    public String createNewWorkflow(WorkflowInstance workflow) {
        mongoOperation.insert(workflow);
        return workflow.getId();
    }

    @Override
    public void persistWorkflow(WorkflowInstance workflow) {
        //WorkflowInstance e = mongoOperation.findById(workflow.getId(), WorkflowInstance.class);
        mongoOperation.save(workflow);
    }

    @Override
    public Iterable<String> getRunnableInstances() {
        
        Query query = new Query();        
        query.addCriteria(Criteria.where("nextExecution").lte(new Date().getTime()));
        query.addCriteria(Criteria.where("status").is(WorkflowStatus.RUNNABLE));
                
        List<WorkflowInstance> result = mongoOperation.find(query, WorkflowInstance.class);
        
        return Arrays.asList(result.stream().map(e -> e.getId()).toArray(String[]::new));
    }

    @Override
    public WorkflowInstance getWorkflowInstance(String id) {
        return mongoOperation.findById(id, WorkflowInstance.class);
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
    public String createEvent(Event newEvent) {
        mongoOperation.insert(newEvent);
        return newEvent.id;
    }

    @Override
    public Event getEvent(String id) {
        return mongoOperation.findById(id, Event.class);
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
