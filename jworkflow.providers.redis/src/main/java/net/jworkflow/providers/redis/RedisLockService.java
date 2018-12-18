package net.jworkflow.providers.redis;

import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.LockService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisLockService implements LockService {

    private final RedissonClient client;
    private final Logger logger;
    
    public RedisLockService(Config config, Logger logger) {
        this.logger = logger;
        client = Redisson.create(config);
    }
    
    @Override
    public boolean acquireLock(String id) {
        RLock lock = client.getLock(id);
        
    }

    @Override
    public void releaseLock(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        
    }
    
}
