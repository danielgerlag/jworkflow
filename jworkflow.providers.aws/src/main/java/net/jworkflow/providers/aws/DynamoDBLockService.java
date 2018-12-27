package net.jworkflow.providers.aws;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import com.google.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.LockService;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.TableStatus;

@Singleton
public class DynamoDBLockService implements LockService {

    private final String tableName;
    private final Region region;
    private final String nodeId;
    private DynamoDbClient client;
    private final long ttl = 30000;
    private final long heartbeat = 10000;
    private final long jitter = 1000;
    private final ScheduledExecutorService scheduler;
    private final List<String> localLocks;
    private ScheduledFuture heartbeatFuture;

    
    public DynamoDBLockService(Region region, String tableName) {
        this.region = region;
        this.tableName = tableName;
        this.nodeId = UUID.randomUUID().toString();
        this.localLocks = new ArrayList<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    @Override
    public synchronized boolean acquireLock(String id) {
        if (client == null)
            throw new IllegalStateException();
        
        Map<String, AttributeValue> item = buildIdMap(id);
        item.put("lockOwner", AttributeValue.builder().s(nodeId).build());
        item.put("expires", AttributeValue.builder().n(String.valueOf(Instant.now().toEpochMilli() + ttl)).build());
        
        Map<String, AttributeValue> condValues = new HashMap<>();
        condValues.put(":expired", AttributeValue.builder().n(String.valueOf(Instant.now().toEpochMilli() + jitter)).build());
                
        try {
            PutItemResponse lock1Resp = client.putItem(x -> x
                .tableName(tableName)
                .conditionExpression("attribute_not_exists(id) OR (expires < :expired)")
                .expressionAttributeValues(condValues)
                .item(item)
            );
            
            if (lock1Resp.sdkHttpResponse().isSuccessful()) {
                localLocks.add(id);
                return true;
            }
        }
        catch (ConditionalCheckFailedException ex) {
            Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.FINE, "Failed to get lock {0}", id);
        }
        return false;
    }

    @Override
    public synchronized void releaseLock(String id) {
        if (client == null)
            throw new IllegalStateException();
        
        localLocks.remove(id);
        
        Map<String, AttributeValue> cv = new HashMap<>();
        cv.put(":nodeId", AttributeValue.builder().s(nodeId).build());
        
        try {
            client.deleteItem(x -> x.
                tableName(tableName)
                .key(buildIdMap(id))
                .conditionExpression("lockOwner = :nodeId")
                .expressionAttributeValues(cv)
            );
        }
        catch (ConditionalCheckFailedException ex) {
            Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.FINE, "Failed to release lock {0}", id);
        }
    }

    @Override
    public void start() {
        client = DynamoDbClient.builder()
                .region(region)
                .build();
                
        ensureTable();        
        heartbeatFuture = scheduler.scheduleAtFixedRate(() -> sendHeartbeat(), heartbeat, heartbeat, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        heartbeatFuture.cancel(true);
        client.close();
    }
    
    private Map<String, AttributeValue> buildIdMap(String id) {
        Map<String, AttributeValue> result = new HashMap<>();
        result.put("id", AttributeValue.builder().s(id).build());
        return result;
    }
    
    private synchronized void sendHeartbeat() { 
        try {
            for (String lock: localLocks) {                    
                Map<String, AttributeValue> item = buildIdMap(lock);
                item.put("lockOwner", AttributeValue.builder().s(nodeId).build());
                item.put("expires", AttributeValue.builder().n(String.valueOf(Instant.now().toEpochMilli() + ttl)).build());

                Map<String, AttributeValue> cv = new HashMap<>();
                cv.put(":nodeId", AttributeValue.builder().s(nodeId).build());                    

                client.putItem(x -> x
                    .tableName(tableName)
                    .conditionExpression("lockOwner = :nodeId")
                    .expressionAttributeValues(cv)
                    .item(item)
                );
            }
        }
        catch (Exception ex) {
            Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.WARNING, "Error sending heartbeat", ex);
        }
    }
    
    private void ensureTable() {
        if (client == null)
            throw new IllegalStateException();
        
        try {
            DescribeTableResponse r = client.describeTable(x -> x.tableName(tableName));
        } 
        catch (ResourceNotFoundException ex) {
            createTable();
        }
    }

    private void createTable() throws AwsServiceException, SdkClientException {
        if (client == null)
            throw new IllegalStateException();
        
        Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.INFO, "Creating lock table in DynamoDB");
        
        client.createTable(x -> x
            .tableName(tableName)
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
                Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.SEVERE, null, ex);
            }
            DescribeTableResponse r = client.describeTable(x -> x.tableName(tableName));
            created = (r.table().tableStatus() == TableStatus.ACTIVE);
            i++;
        }
    }
}