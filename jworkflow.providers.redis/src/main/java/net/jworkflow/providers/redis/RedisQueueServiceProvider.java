package net.jworkflow.providers.redis;

import com.google.inject.Provider;
import org.redisson.config.Config;

public class RedisQueueServiceProvider implements Provider<RedisQueueService> {

    private final Config config;
    
    public RedisQueueServiceProvider(Config config) {
        this.config = config;
    }
    
    @Override
    public RedisQueueService get() {
        return new RedisQueueService(config);
    }
    
}
