package net.jworkflow.providers.aws;

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.PersistenceService;
import net.jworkflow.kernel.models.Event;
import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.WorkflowStatus;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.Select;

public class DynamoDBPersistenceService implements PersistenceService {

    public static final String WORKFLOW_TABLE = "workflows";
    public static final String SUBSCRIPTION_TABLE = "subscriptions";
    public static final String EVENT_TABLE = "events";
    
    private final String tablePrefix;
    private final DynamoDbClient client;
    private final DynamoDBProvisioner provisioner;
    private final Gson gson;
    
    public DynamoDBPersistenceService(Region region, DynamoDBProvisioner provisioner, String tablePrefix) {
        client = DynamoDbClient.builder()
                .region(region)
                .build();
        
        this.provisioner = provisioner;
        this.tablePrefix = tablePrefix;
        this.gson = new Gson();
    }
    
    @Override
    public String createNewWorkflow(WorkflowInstance workflow) {
        workflow.setId(UUID.randomUUID().toString());
        
        Map<String, AttributeValue> item = mapFromWorkflow(workflow);
            
        PutItemResponse resp = client.putItem(x -> x
            .tableName(tablePrefix + "-" + WORKFLOW_TABLE)
            .conditionExpression("attribute_not_exists(id)")
            .item(item)                    
        );
        
        return workflow.getId();
    }

    @Override
    public void persistWorkflow(WorkflowInstance workflow) {
        Map<String, AttributeValue> item = mapFromWorkflow(workflow);
            
        PutItemResponse resp = client.putItem(x -> x
            .tableName(tablePrefix + "-" + WORKFLOW_TABLE)
            .item(item)                    
        );
    }

    @Override
    public Iterable<String> getRunnableInstances() {
        Collection<String> result = new ArrayList<>();
                
        Long now = new Date().getTime();
        
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":r", AttributeValue.builder().n("1").build());
        eav.put(":effective_date", AttributeValue.builder().n(now.toString()).build());        
        
        QueryResponse response = client.query(x -> x
            .tableName(tablePrefix + "-" + WORKFLOW_TABLE)
            .indexName("ix_runnable")
            .projectionExpression("id")
            .keyConditionExpression("runnable = :r and next_execution <= :effective_date")
            .scanIndexForward(true)
            .expressionAttributeValues(eav)
        );
        
        response.items().stream().forEach((item) -> {            
            result.add(item.get("id").s());
        });
        
        return result;
    }

    @Override
    public WorkflowInstance getWorkflowInstance(String id) {        
        GetItemResponse response = client.getItem(x -> x
            .tableName(tablePrefix + "-" + WORKFLOW_TABLE)
            .key(buildIdMap(id))
        );
        
        return mapToWorkflow(response.item());                
    }

    @Override
    public String createEventSubscription(EventSubscription subscription) {
        subscription.id = UUID.randomUUID().toString();
        
        Map<String, AttributeValue> item = mapFromSubscription(subscription);
            
        PutItemResponse resp = client.putItem(x -> x
            .tableName(tablePrefix + "-" + SUBSCRIPTION_TABLE)
            .conditionExpression("attribute_not_exists(id)")
            .item(item)                    
        );
        return subscription.id;
    }

    @Override
    public Iterable<EventSubscription> getSubcriptions(String eventName, String eventKey, Date asOf) {
        Collection<EventSubscription> result = new ArrayList<>();
                
        Long asOfMs = asOf.getTime();
        
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":slug", AttributeValue.builder().s(eventName + ":" + eventKey).build());
        eav.put(":as_of", AttributeValue.builder().n(asOfMs.toString()).build());        
        
        QueryResponse response = client.query(x -> x
            .tableName(tablePrefix + "-" + SUBSCRIPTION_TABLE)
            .indexName("ix_slug")
            .select(Select.ALL_PROJECTED_ATTRIBUTES)
            .keyConditionExpression("event_slug = :slug and subscribe_as_of <= :as_of")
            .scanIndexForward(true)
            .expressionAttributeValues(eav)
        );
        
        response.items().stream().forEach((item) -> {            
            result.add(mapToSubscription(item));
        });
        
        return result;
    }

    @Override
    public void terminateSubscription(String eventSubscriptionId) {
        client.deleteItem(x -> x
            .tableName(tablePrefix + "-" + SUBSCRIPTION_TABLE)
            .key(buildIdMap(eventSubscriptionId))
        );
    }

    @Override
    public String createEvent(Event newEvent) {
        newEvent.id = UUID.randomUUID().toString();
        
        Map<String, AttributeValue> item = mapFromEvent(newEvent);
            
        PutItemResponse resp = client.putItem(x -> x
            .tableName(tablePrefix + "-" + EVENT_TABLE)
            .conditionExpression("attribute_not_exists(id)")
            .item(item)                    
        );
        return newEvent.id;
    }

    @Override
    public Event getEvent(String id) {
        GetItemResponse response = client.getItem(x -> x
            .tableName(tablePrefix + "-" + EVENT_TABLE)
            .key(buildIdMap(id))
        );
        
        return mapToEvent(response.item());
    }

    @Override
    public Iterable<String> getRunnableEvents() {
        Collection<String> result = new ArrayList<>();
                
        Long now = new Date().getTime();
        
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":n", AttributeValue.builder().n("1").build());
        eav.put(":effective_date", AttributeValue.builder().n(now.toString()).build());        
        
        QueryResponse response = client.query(x -> x
            .tableName(tablePrefix + "-" + EVENT_TABLE)
            .indexName("ix_not_processed")
            .projectionExpression("id")
            .keyConditionExpression("not_processed = :n and event_time <= :effective_date")
            .scanIndexForward(true)
            .expressionAttributeValues(eav)
        );
        
        response.items().stream().forEach((item) -> {            
            result.add(item.get("id").s());
        });
        
        return result;
    }

    @Override
    public Iterable<String> getEvents(String eventName, String eventKey, Date asOf) {
        Collection<String> result = new ArrayList<>();
                
        Long asOfMs = asOf.getTime();
        
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":slug", AttributeValue.builder().s(eventName + ":" + eventKey).build());
        eav.put(":effective_date", AttributeValue.builder().n(asOfMs.toString()).build());        
        
        QueryResponse response = client.query(x -> x
            .tableName(tablePrefix + "-" + EVENT_TABLE)
            .indexName("ix_slug")
            .projectionExpression("id")
            .keyConditionExpression("event_slug = :slug and event_time >= :effective_date")
            .scanIndexForward(true)
            .expressionAttributeValues(eav)
        );
        
        response.items().stream().forEach((item) -> {            
            result.add(item.get("id").s());
        });
        
        return result;
    }

    @Override
    public void markEventProcessed(String id) {
        client.updateItem(x -> x
            .tableName(tablePrefix + "-" + EVENT_TABLE)
            .key(buildIdMap(id))
            .updateExpression("REMOVE not_processed")
        );
    }

    @Override
    public void markEventUnprocessed(String id) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":n", AttributeValue.builder().n("1").build());
                
        client.updateItem(x -> x
            .tableName(tablePrefix + "-" + EVENT_TABLE)
            .key(buildIdMap(id))
            .updateExpression("ADD not_processed = :n")
            .expressionAttributeValues(eav)
        );
    }

    @Override
    public void provisionStore() {
        provisioner.ensureTables();
    }    
    
    private Map<String, AttributeValue> mapFromWorkflow(WorkflowInstance source) {
        Map<String, AttributeValue> result = new HashMap<>();
                
        result.put("id", AttributeValue.builder().s(source.getId()).build());
        result.put("workflow_status", AttributeValue.builder().s(source.getStatus().toString()).build());
        result.put("workflow_definition_id", AttributeValue.builder().s(source.getWorkflowDefintionId()).build());
        
        if (source.getNextExecution() != null)
            result.put("next_exectution", AttributeValue.builder().n(source.getNextExecution().toString()).build());
        
        if (source.getStatus() == WorkflowStatus.RUNNABLE)
            result.put("runnable", AttributeValue.builder().n("1").build());
        
        result.put("instance", AttributeValue.builder().s(gson.toJson(source)).build());
        
        /*
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(source);
            out.flush();        
            result.put("instance", AttributeValue.builder().b(SdkBytes.fromByteArray(baos.toByteArray())).build());
        }
        */
        return result;        
    }
    
    private WorkflowInstance mapToWorkflow(Map<String, AttributeValue> source) {
        
        return gson.fromJson(source.get("instance").s(), WorkflowInstance.class);
        /*
        try (ByteArrayInputStream bis = new ByteArrayInputStream(source.get("instance").b().asByteArray());
            ObjectInput in = new ObjectInputStream(bis)) {
            return (WorkflowInstance)(in.readObject());
        } 
        */
    }
    
    private Map<String, AttributeValue> mapFromSubscription(EventSubscription source) {
        Map<String, AttributeValue> result = new HashMap<>();
                
        result.put("id", AttributeValue.builder().s(source.id).build());
        result.put("event_name", AttributeValue.builder().s(source.eventName).build());
        result.put("event_key", AttributeValue.builder().s(source.eventKey).build());
        result.put("workflow_id", AttributeValue.builder().s(source.workflowId).build());
        result.put("step_id", AttributeValue.builder().s(String.valueOf(source.stepId)).build());
        result.put("subscribe_as_of", AttributeValue.builder().n(String.valueOf(source.subscribeAsOfUtc.getTime())).build());
        result.put("event_slug", AttributeValue.builder().s(source.eventName + ":" + source.eventKey).build());
        
        return result;
    }
    
    private EventSubscription mapToSubscription(Map<String, AttributeValue> source) {
        EventSubscription result = new EventSubscription();
        result.id = source.get("id").s();
        result.eventName = source.get("event_name").s();
        result.eventKey = source.get("event_key").s();
        result.workflowId = source.get("workflow_id").s();
        result.stepId = Integer.parseInt(source.get("step_id").s());
        
        Long asOfMs = Long.parseLong(source.get("subscribe_as_of").n());
        result.subscribeAsOfUtc = new Date(asOfMs);
        
        return result;
    }
    
    private Map<String, AttributeValue> mapFromEvent(Event source) {
        Map<String, AttributeValue> result = new HashMap<>();
                
        result.put("id", AttributeValue.builder().s(source.id).build());
        result.put("event_name", AttributeValue.builder().s(source.eventName).build());
        result.put("event_key", AttributeValue.builder().s(source.eventKey).build());
        result.put("event_data", AttributeValue.builder().s(gson.toJson(source.eventData)).build());
        //result.put("event_data_class", AttributeValue.builder().s("").build());
        result.put("event_time", AttributeValue.builder().n(String.valueOf(source.eventTimeUtc.getTime())).build());
        result.put("event_slug", AttributeValue.builder().s(source.eventName + ":" + source.eventKey).build());
        
        if (!source.isProcessed)
            result.put("not_processed", AttributeValue.builder().n("1").build());
        
        return result;
    }
    
    private Event mapToEvent(Map<String, AttributeValue> source) {
        Event result = new Event();
        result.id = source.get("id").s();
        result.eventName = source.get("event_name").s();
        result.eventKey = source.get("event_key").s();
        result.eventData = gson.fromJson(source.get("workflow_id").s(), Object.class);
        result.isProcessed = !source.containsKey("not_processed");
        
        //Class.forName("")
        Long asOfMs = Long.parseLong(source.get("event_time").n());        
        result.eventTimeUtc = new Date(asOfMs);
        
        return result;
    }
    
    private Map<String, AttributeValue> buildIdMap(String id) {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put("id", AttributeValue.builder().s(id).build());
        return result;
    }
    
}
