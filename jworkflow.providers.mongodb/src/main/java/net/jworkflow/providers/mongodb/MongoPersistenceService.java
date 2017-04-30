package net.jworkflow.providers.mongodb;

import net.jworkflow.kernel.models.*;
import com.mongodb.MongoClientURI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import net.jworkflow.kernel.interfaces.PersistenceService;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.index.Index;

public class MongoPersistenceService implements PersistenceService {

    public static MongoPersistenceServiceProvider configure(String uri) {
        return new MongoPersistenceServiceProvider(uri);
    }
    
    private final MongoOperations mongoOperation;
    private static boolean indexesChecked = false;
    
    public MongoPersistenceService(String uri) throws UnknownHostException {
        SimpleMongoDbFactory factory = new SimpleMongoDbFactory(new MongoClientURI(uri));
        mongoOperation = new MongoTemplate(factory);
        ensureIndexes();
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
    
    private synchronized void ensureIndexes() {
        if (!indexesChecked) {
            mongoOperation.indexOps(WorkflowInstance.class).ensureIndex(new Index().on("nextExecution", Direction.ASC));
            mongoOperation.indexOps(WorkflowInstance.class).ensureIndex(new Index().on("status", Direction.ASC));        
            
            mongoOperation.indexOps(Event.class).ensureIndex(new Index().on("eventName", Direction.ASC));
            mongoOperation.indexOps(Event.class).ensureIndex(new Index().on("eventKey", Direction.ASC));
            mongoOperation.indexOps(Event.class).ensureIndex(new Index().on("eventTimeUtc", Direction.ASC));
            mongoOperation.indexOps(Event.class).ensureIndex(new Index().on("isProcessed", Direction.ASC));
            
            mongoOperation.indexOps(EventSubscription.class).ensureIndex(new Index().on("eventName", Direction.ASC));
            mongoOperation.indexOps(EventSubscription.class).ensureIndex(new Index().on("eventKey", Direction.ASC));
            
            indexesChecked = true;
        }
    }    
}
