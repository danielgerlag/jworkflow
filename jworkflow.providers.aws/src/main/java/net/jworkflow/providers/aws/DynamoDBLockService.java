/*
package net.jworkflow.providers.aws;

import com.amazonaws.services.dynamodbv2.AcquireLockOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import com.amazonaws.services.dynamodbv2.CreateDynamoDBTableOptions;
import com.amazonaws.services.dynamodbv2.LockItem;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.LockService;
import software.amazon.awssdk.regions.Region;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Singleton
public class DynamoDBLockService implements LockService {

    private final String tableName;
    private final Region region;
    private final Map<String, LockItem> localLocks;
    private AmazonDynamoDB dynamoDB;    
    private AmazonDynamoDBLockClient client;
    
    public DynamoDBLockService(Region region, String tableName) {
        this.region = region;
        this.tableName = tableName;
        localLocks = new ConcurrentHashMap<>();
    }
    
    @Override
    public boolean acquireLock(String id) {
        AcquireLockOptions opts = AcquireLockOptions
                .builder(id)                
                .build();
        
        try {
            Optional<LockItem> lock = client.tryAcquireLock(opts);
            if (!lock.isPresent())
                return false;

            localLocks.put(id, lock.get());
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }        
    }

    @Override
    public void releaseLock(String id) {
        LockItem lock = localLocks.get(id);
        if (lock != null) {
            client.releaseLock(lock);
            localLocks.remove(id);
        }
    }

    @Override
    public void start() {
        dynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(region.id())                        
                    .build();
        
        client = new AmazonDynamoDBLockClient(AmazonDynamoDBLockClientOptions.builder(dynamoDB, tableName)
                    .withTimeUnit(TimeUnit.SECONDS)
                    .withLeaseDuration(10L)
                    .withHeartbeatPeriod(3L)                    
                    .build());
        
        if (!client.lockTableExists()) {
            ProvisionedThroughput ptp = new ProvisionedThroughput()
                    .withReadCapacityUnits(5L)
                    .withWriteCapacityUnits(5L);
            
            CreateDynamoDBTableOptions opts = CreateDynamoDBTableOptions.builder(dynamoDB, ptp, tableName).build();
            AmazonDynamoDBLockClient.createLockTableInDynamoDB(opts);
        }
    }

    @Override
    public void stop() {
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(DynamoDBLockService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
*/