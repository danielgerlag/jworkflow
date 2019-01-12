package net.jworkflow.providers.aws;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

public class DefaultDynamoDBProvisioner implements DynamoDBProvisioner {
    
    private final DynamoDbClient client;
    private final String prefix;
    private final ProvisionedThroughput defaultThroughput;    
    
    public DefaultDynamoDBProvisioner(Region region, String prefix) {
        
        client = DynamoDbClient.builder()
            .region(region)
            .build();
        
        defaultThroughput = ProvisionedThroughput.builder()
            .readCapacityUnits(1L)
            .writeCapacityUnits(1L)
            .build();
    
        this.prefix = prefix;
    }
    
    @Override
    public void ensureTables() throws AwsServiceException, SdkClientException {
        if (!tableExists(prefix + "-" + DynamoDBPersistenceService.WORKFLOW_TABLE))
            createTable(buildWorkflowTableRequest());
        
        if (!tableExists(prefix + "-" + DynamoDBPersistenceService.SUBSCRIPTION_TABLE))
            createTable(buildSubscriptionTableRequest());
        
        if (!tableExists(prefix + "-" + DynamoDBPersistenceService.EVENT_TABLE))
            createTable(buildEventTableRequest());
    }
    
    
    private CreateTableRequest buildWorkflowTableRequest() {        
        
        GlobalSecondaryIndex runnableIx = GlobalSecondaryIndex.builder()
            .indexName("ix_runnable")
            .keySchema(Arrays.asList(
                KeySchemaElement.builder()
                    .attributeName("runnable")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("next_execution")
                    .keyType(KeyType.RANGE)
                    .build()
            ))
            .projection(x -> x.projectionType(ProjectionType.KEYS_ONLY))
            //.provisionedThroughput(defaultThroughput)
            .build();        
        
        return CreateTableRequest.builder()
            .tableName(prefix + "-" + DynamoDBPersistenceService.WORKFLOW_TABLE)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .keySchema(key -> key
                .attributeName("id")
                .keyType(KeyType.HASH))
            .attributeDefinitions(Arrays.asList(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("runnable")
                    .attributeType(ScalarAttributeType.N)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("next_execution")
                    .attributeType(ScalarAttributeType.N)
                    .build()
            ))
            .globalSecondaryIndexes(runnableIx)
            //.provisionedThroughput(defaultThroughput)
            .build();
    }
    
    private CreateTableRequest buildSubscriptionTableRequest() {        
        
        GlobalSecondaryIndex slugIx = GlobalSecondaryIndex.builder()
            .indexName("ix_slug")
            .keySchema(Arrays.asList(
                KeySchemaElement.builder()
                    .attributeName("event_slug")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("subscribe_as_of")
                    .keyType(KeyType.RANGE)
                    .build()
            ))
            .projection(x -> x.projectionType(ProjectionType.ALL))
            //.provisionedThroughput(defaultThroughput)
            .build();        
        
        return CreateTableRequest.builder()
            .tableName(prefix + "-" + DynamoDBPersistenceService.SUBSCRIPTION_TABLE)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .keySchema(key -> key
                .attributeName("id")
                .keyType(KeyType.HASH))
            .attributeDefinitions(Arrays.asList(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("event_slug")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("subscribe_as_of")
                    .attributeType(ScalarAttributeType.N)
                    .build()
            ))
            .globalSecondaryIndexes(slugIx)
            //.provisionedThroughput(defaultThroughput)
            .build();
    }
    
    private CreateTableRequest buildEventTableRequest() {        
        
        GlobalSecondaryIndex slugIx = GlobalSecondaryIndex.builder()
            .indexName("ix_slug")
            .keySchema(Arrays.asList(
                KeySchemaElement.builder()
                    .attributeName("event_slug")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("event_time")
                    .keyType(KeyType.RANGE)
                    .build()
            ))
            .projection(x -> x.projectionType(ProjectionType.KEYS_ONLY))
            //.provisionedThroughput(defaultThroughput)
            .build();
        
        GlobalSecondaryIndex processedIx = GlobalSecondaryIndex.builder()
            .indexName("ix_not_processed")
            .keySchema(Arrays.asList(
                KeySchemaElement.builder()
                    .attributeName("not_processed")
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName("event_time")
                    .keyType(KeyType.RANGE)
                    .build()
            ))
            .projection(x -> x.projectionType(ProjectionType.KEYS_ONLY))
            //.provisionedThroughput(defaultThroughput)
            .build();
        
        return CreateTableRequest.builder()
            .tableName(prefix + "-" + DynamoDBPersistenceService.EVENT_TABLE)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .keySchema(key -> key
                .attributeName("id")
                .keyType(KeyType.HASH))
            .attributeDefinitions(Arrays.asList(
                AttributeDefinition.builder()
                    .attributeName("id")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("event_slug")
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("event_time")
                    .attributeType(ScalarAttributeType.N)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName("not_processed")
                    .attributeType(ScalarAttributeType.N)
                    .build()
            ))
            .globalSecondaryIndexes(Arrays.asList(slugIx, processedIx))
            //.provisionedThroughput(defaultThroughput)
            .build();
    }
    
    private void createTable(CreateTableRequest request) throws AwsServiceException, SdkClientException {
        if (client == null)
            throw new IllegalStateException();
        
        Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.INFO, "Creating {0} table in DynamoDB", request.tableName());
        
        client.createTable(request);
        
        int i = 0;
        boolean created = false;
        while ((i < 10) && (!created)) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.SEVERE, null, ex);
            }
            DescribeTableResponse r = client.describeTable(x -> x.tableName(request.tableName()));
            created = (r.table().tableStatus() == TableStatus.ACTIVE);
            i++;
        }
    }
    
    private boolean tableExists(String tableName) {
        if (client == null)
            throw new IllegalStateException();
        
        try {
            DescribeTableResponse r = client.describeTable(x -> x.tableName(tableName));
        } 
        catch (ResourceNotFoundException ex) {
            return false;
        }
        return true;
    }
}
