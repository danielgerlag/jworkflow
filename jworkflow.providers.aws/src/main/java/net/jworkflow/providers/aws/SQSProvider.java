package net.jworkflow.providers.aws;

import com.google.inject.Provider;
import software.amazon.awssdk.regions.Region;

public class SQSProvider implements Provider<SQSQueueService> {

    private final Region region;
    
    public SQSProvider(Region region) {
        this.region = region;
    }
    
    @Override
    public SQSQueueService get() {
        return new SQSQueueService(region);
    }
    
}
