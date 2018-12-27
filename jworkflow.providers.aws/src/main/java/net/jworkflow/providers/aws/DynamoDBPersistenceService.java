package net.jworkflow.providers.aws;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.PersistenceService;
import net.jworkflow.kernel.models.Event;
import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.WorkflowStatus;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

public class DynamoDBPersistenceService implements PersistenceService {

    private final String workflowTableName;
    private final String tablePrefix = "jworkflow-";
    private final DynamoDbClient client;
    
    
    public DynamoDBPersistenceService(Region region) {
        this.workflowTableName = tablePrefix + "workflows";
        
        client = DynamoDbClient.builder()
                .region(region)
                .build();
        
    }
    
    @Override
    public String createNewWorkflow(WorkflowInstance workflow) {
        
//software.amazon.awssdk.services.dynamodb.model.
    }

    @Override
    public void persistWorkflow(WorkflowInstance workflow) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<String> getRunnableInstances() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorkflowInstance getWorkflowInstance(String id) {
        GetItemResponse response = client.getItem(x -> x
            .tableName(workflowTableName)
        );
        
        //response.item()
        //response.
                
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


    private void ensureTables() {
        try {
            client.describeTable(x -> x.tableName(workflowTableName));
        } 
        catch (ResourceNotFoundException ex) {
            createTables();
        }
    }

    private void createTables() throws AwsServiceException, SdkClientException {
        Logger.getLogger(DynamoDBPersistenceService.class.getName()).log(Level.INFO, "Creating tables in DynamoDB");
        
        client.createTable(x -> x
            .tableName(workflowTableName)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .keySchema(key -> key
                    .attributeName("id")
                    .keyType(KeyType.HASH))
            .attributeDefinitions(attr -> attr
                    .attributeName("id")
                    .attributeType("S"))
        );
        
        int i = 0;
        boolean created = false;
        while ((i < 10) && (!created)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DynamoDBPersistenceService.class.getName()).log(Level.SEVERE, null, ex);
            }
            DescribeTableResponse r = client.describeTable(x -> x.tableName(workflowTableName));
            created = (r.table().tableStatus() == TableStatus.ACTIVE);
            i++;
        }
    }
    
    
    private DynamoDBFluentMapper<ExecutionPointer> buildExecutionPointerMapper() { 
        return new DynamoDBFluentMapper<>(ExecutionPointer.class)
                .withString("id", (o) -> o.id, (o, v) -> o.id = v)
                .withInteger("stepId", (o) -> o.stepId, (o, v) -> o.stepId = v)
                .withBool("id", (o) -> o.active, (o, v) -> o.active = v)
                .withString("id", (o) -> o.callStack, (o, v) -> o.id = v)
                .withString("id", (o) -> o.id, (o, v) -> o.id = v)
                .withString("id", (o) -> o.id, (o, v) -> o.id = v)
                
                
                ;
    }
    
    private DynamoDBFluentMapper<WorkflowInstance> buildMapper() {
        return new DynamoDBFluentMapper<>(WorkflowInstance.class)
            .withString("id", (o) -> o.getId(), (o, v) -> o.setId(v))
            .withString("description", (o) -> o.getDescription(), (o, v) -> o.setDescription(v))
            .withString("workflowDefintionId", (o) -> o.getWorkflowDefintionId(), (o, v) -> o.setWorkflowDefintionId(v))
            .withInteger("version", (o) -> o.getVersion(), (o, v) -> o.setVersion((int) v))
            //.withMap("data", (o) -> o.getData(), (o, v) -> o.setData(v))
            
            //.withString("createTimeUtc", (o) -> o.getCreateTimeUtc(), (o, v) -> o.setCreateTimeUtc(v))
                .withLong("nextExecution", (o) -> o.getNextExecution(), (o, v) -> o.setNextExecution(v))
                .withString("status", (o) -> o.getStatus().toString(), (o, v) -> o.setStatus(WorkflowStatus.valueOf(v)))
                .withString("id", (o) -> o.getId(), (o, v) -> o.setId(v))
                .withString("id", (o) -> o.getId(), (o, v) -> o.setId(v));
                
                 
    }
    
    /*
    private static Map<String, AttributeValue> buildWorkflowMap(WorkflowInstance workflow) {
        Map<String, AttributeValue> result = new HashMap<>();
        
        result.put("id", AttributeValue.builder().s(workflow.getId()).build());
        result.put("description", AttributeValue.builder().s(workflow.getDescription()).build());        
        result.put("workflowDefintionId", AttributeValue.builder().s(workflow.getWorkflowDefintionId()).build());        
        result.put("version", AttributeValue.builder().n(String.valueOf(workflow.getVersion())).build());        
        result.put("data", AttributeValue.builder().b(workflow.getData()).build());        
        result.put("createTimeUtc", AttributeValue.builder().s(workflow.getCreateTimeUtc()).build());
        result.put("completeTimeUtc", AttributeValue.builder().s(workflow.getCompleteTimeUtc()).build());        
        result.put("nextExecution", AttributeValue.builder().n(String.valueOf(workflow.getNextExecution())).build());
        result.put("status", AttributeValue.builder().s(workflow.getStatus().toString()).build());
        
        Collection<AttributeValue> pointers = new List<>();
        
        for (ExecutionPointer ep: workflow.getExecutionPointers()) {
            Map<String, AttributeValue> epResult = new HashMap<>();
            
            epResult.put("id", AttributeValue.builder().s(ep.id).build());        
            
            pointers.add(AttributeValue.builder().m(epResult).build());
        }
        
        result.put("executionPointers", AttributeValue.builder().l(pointers).build());
        
        
        return result;
    }
    */
}
