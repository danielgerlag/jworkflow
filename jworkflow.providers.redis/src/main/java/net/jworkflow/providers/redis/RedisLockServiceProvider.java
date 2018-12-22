package net.jworkflow.providers.redis;

import com.google.inject.Provider;
import org.redisson.config.Config;

public class RedisLockServiceProvider implements Provider<RedisLockService> {

    private final Config config;
    
    public RedisLockServiceProvider(Config config) {
        this.config = config;
    }
    
    @Override
    public RedisLockService get() {
        return new RedisLockService(config);
    }
    
}
