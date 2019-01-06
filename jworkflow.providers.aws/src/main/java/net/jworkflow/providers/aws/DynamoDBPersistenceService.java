package net.jworkflow.providers.aws;

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

public class DynamoDBPersistenceService implements PersistenceService {

    public static String workflowTableName = "workflows";
    private final String tablePrefix;
    private final DynamoDbClient client;
    private final DynamoDBProvisioner provisioner;
    
    
    public DynamoDBPersistenceService(Region region, DynamoDBProvisioner provisioner, String tablePrefix) {
        client = DynamoDbClient.builder()
                .region(region)
                .build();
        
        this.provisioner = provisioner;
        this.tablePrefix = tablePrefix;
    }
    
    @Override
    public String createNewWorkflow(WorkflowInstance workflow) {
        workflow.setId(UUID.randomUUID().toString());
        
        try {
            Map<String, AttributeValue> item = mapFromWorkflow(workflow);
            
            PutItemResponse resp = client.putItem(x -> x
                .tableName(tablePrefix + "-" + workflowTableName)
                .conditionExpression("attribute_not_exists(id)")
                .item(item)                    
            );            
            
        } catch (IOException ex) {
            Logger.getLogger(DynamoDBPersistenceService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        return workflow.getId();
    }

    @Override
    public void persistWorkflow(WorkflowInstance workflow) {
        try {
            Map<String, AttributeValue> item = mapFromWorkflow(workflow);
            
            PutItemResponse resp = client.putItem(x -> x
                .tableName(tablePrefix + "-" + workflowTableName)
                .item(item)                    
            );            
            
        } catch (IOException ex) {
            Logger.getLogger(DynamoDBPersistenceService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Iterable<String> getRunnableInstances() {
        Collection<String> result = new ArrayList<>();
                
        Long now = new Date().getTime();
        
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":r", AttributeValue.builder().s("1").build());
        eav.put(":effective_date", AttributeValue.builder().n(now.toString()).build());        
        
        QueryResponse response = client.query(x -> x
            .tableName(tablePrefix + "-" + workflowTableName)
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
            .tableName(tablePrefix + "-" + workflowTableName)
            .key(buildIdMap(id))
        );
        
        try {
            return mapToWorkflow(response.item());
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DynamoDBPersistenceService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }                
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Event getEvent(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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


    @Override
    public void provisionStore() {
        provisioner.ensureTables();
    }    
    
    private Map<String, AttributeValue> mapFromWorkflow(WorkflowInstance source) throws IOException {        
        Map<String, AttributeValue> result = new HashMap<>();
                
        result.put("id", AttributeValue.builder().s(source.getId()).build());
        result.put("next_exectution", AttributeValue.builder().n(source.getNextExecution().toString()).build());
        
        if (source.getStatus() == WorkflowStatus.RUNNABLE)
            result.put("runnable", AttributeValue.builder().n("1").build());
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(baos);
            out.writeObject(source);
            out.flush();        
            result.put("instance", AttributeValue.builder().b(SdkBytes.fromByteArray(baos.toByteArray())).build());
        }
        
        return result;        
    }
    
    private WorkflowInstance mapToWorkflow(Map<String, AttributeValue> source) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(source.get("instance").b().asByteArray());
            ObjectInput in = new ObjectInputStream(bis)) {
            return (WorkflowInstance)(in.readObject());
        } 
    }
    
    private Map<String, AttributeValue> buildIdMap(String id) {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put("id", AttributeValue.builder().s(id).build());
        return result;
    }
    
}
