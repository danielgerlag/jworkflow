package com.jworkflow.providers.mongodb;

import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.models.Event;
import com.jworkflow.kernel.models.EventSubscription;
import com.jworkflow.kernel.models.WorkflowInstance;
import com.jworkflow.kernel.models.WorkflowStatus;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class MongoPersistenceProvider implements PersistenceProvider {

    MongoOperations mongoOperation;
    
    public MongoPersistenceProvider(String uri) throws UnknownHostException {
        SimpleMongoDbFactory factory = new SimpleMongoDbFactory(new MongoClientURI(uri));
        mongoOperation = new MongoTemplate(factory);
    }    
    
    @Override
    public String createNewWorkflow(WorkflowInstance workflow) {
        mongoOperation.insert(workflow);
        return workflow.getId();
    }

    @Override
    public void persistWorkflow(WorkflowInstance workflow) {
        mongoOperation.save(workflow);
    }

    @Override
    public Iterable<String> getRunnableInstances() {        
        Query query = new Query();        
        query.addCriteria(Criteria.where("nextExecution").lte(new Date().getTime()));
        query.addCriteria(Criteria.where("status").is(WorkflowStatus.RUNNABLE));
        query.fields().include("id");
        
        List<WorkflowInstance> result = mongoOperation.find(query, WorkflowInstance.class);
        
        return Arrays.asList(result.stream().map(e -> e.getId()).toArray(String[]::new));
    }

    @Override
    public WorkflowInstance getWorkflowInstance(String id) {
        return mongoOperation.findById(id, WorkflowInstance.class);
    }

    @Override
    public String createEventSubscription(EventSubscription subscription) {
        mongoOperation.insert(subscription);
        return subscription.id;
    }

    @Override
    public Iterable<EventSubscription> getSubcriptions(String eventName, String eventKey, Date asOf) {
        Query query = new Query();        
        query.addCriteria(Criteria.where("eventName").is(eventName));
        query.addCriteria(Criteria.where("eventKey").is(eventKey));
        query.addCriteria(Criteria.where("subscribeAsOfUtc").lte(asOf));
                
        List<EventSubscription> result = mongoOperation.find(query, EventSubscription.class);
        return result;
    }

    @Override
    public void terminateSubscription(String eventSubscriptionId) {
        Query query = new Query();        
        query.addCriteria(Criteria.where("id").is(eventSubscriptionId));
        mongoOperation.remove(query, EventSubscription.class);
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
        Query query = new Query();        
        query.addCriteria(Criteria.where("isProcessed").is(false));
        query.addCriteria(Criteria.where("eventTimeUtc").lte(new Date()));
        query.fields().include("id");
        
        List<Event> result = mongoOperation.find(query, Event.class);
        
        return Arrays.asList(result.stream().map(e -> e.id).toArray(String[]::new));
    }

    @Override
    public Iterable<String> getEvents(String eventName, String eventKey, Date asOf) {
        Query query = new Query();        
        query.addCriteria(Criteria.where("eventName").is(eventName));
        query.addCriteria(Criteria.where("eventKey").is(eventKey));
        query.addCriteria(Criteria.where("eventTimeUtc").gte(asOf));
        query.fields().include("id");
        
        List<Event> result = mongoOperation.find(query, Event.class);
        
        return Arrays.asList(result.stream().map(e -> e.id).toArray(String[]::new));
    }

    @Override
    public void markEventProcessed(String id) {
        Query query = new Query();        
        query.addCriteria(Criteria.where("id").is(id));
        mongoOperation.updateFirst(query, new Update().set("isProcessed", true), Event.class);
    }

    @Override
    public void markEventUnprocessed(String id) {
        Query query = new Query();        
        query.addCriteria(Criteria.where("id").is(id));
        mongoOperation.updateFirst(query, new Update().set("isProcessed", false), Event.class);
    }
    
}
