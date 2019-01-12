package net.jworkflow.providers.aws;

import com.google.inject.Provider;
import software.amazon.awssdk.regions.Region;

public class DynamoDBPersistenceProvider implements Provider<DynamoDBPersistenceService> {

    private final Region region;
    private final String tablePrefix;
    
    public DynamoDBPersistenceProvider(Region region, String tablePrefix) {
        this.region = region;
        this.tablePrefix = tablePrefix;
    }
    
    @Override
    public DynamoDBPersistenceService get() {
        DynamoDBProvisioner provisioner = new DefaultDynamoDBProvisioner(region, tablePrefix);
        return new DynamoDBPersistenceService(region, provisioner, tablePrefix);
    }
}