package net.jworkflow.providers.aws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
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

public class DynamoDBProvisioner {
    
    private final DynamoDbClient client;
    private final String prefix;
    private final ProvisionedThroughput defaultThroughput;    
    
    public DynamoDBProvisioner(Region region, String prefix) {
        
        client = DynamoDbClient.builder()
            .region(region)
            .build();
        
        defaultThroughput = ProvisionedThroughput.builder()
            .readCapacityUnits(1L)
            .writeCapacityUnits(1L)
            .build();
    
        this.prefix = prefix;
    }
    
    public void ensureTables() throws AwsServiceException, SdkClientException {
        if (!tableExists(prefix + "-" + DynamoDBPersistenceService.workflowTableName))
            createTable(buildWorkflowTableRequest());
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
            .provisionedThroughput(defaultThroughput)
            .build();        
        
        return CreateTableRequest.builder()
            .tableName(prefix + "-" + DynamoDBPersistenceService.workflowTableName)
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
            .provisionedThroughput(defaultThroughput)
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
                Thread.sleep(1000);
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
