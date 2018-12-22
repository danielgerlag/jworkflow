package net.jworkflow.providers.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import net.jworkflow.kernel.interfaces.LockService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisLockService implements LockService {

    private final RedissonClient client;
    private final Map<String, Lock> localLocks;
    
    public RedisLockService(Config config) {
        localLocks = new HashMap<>();
        client = Redisson.create(config);
    }
    
    @Override
    public boolean acquireLock(String id) {
        Lock lock = client.getLock(id);
        if (!lock.tryLock())
            return false;
        
        localLocks.put(id, lock);
        
        return true;
    }

    @Override
    public void releaseLock(String id) {
        Lock lock = localLocks.get(id);
        if (lock != null)
            lock.unlock();
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        client.shutdown();
    }
    
}
