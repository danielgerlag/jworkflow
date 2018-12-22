/*
package net.jworkflow.providers.aws;

import com.google.inject.Provider;
import software.amazon.awssdk.regions.Region;

public class DynamoDBLockProvider implements Provider<DynamoDBLockService> {

    private final Region region;
    private final String tableName;
    
    public DynamoDBLockProvider(Region region, String tableName) {
        this.region = region;
        this.tableName = tableName;
    }
    
    @Override
    public DynamoDBLockService get() {
        return new DynamoDBLockService(region, tableName);
    }
}
*/