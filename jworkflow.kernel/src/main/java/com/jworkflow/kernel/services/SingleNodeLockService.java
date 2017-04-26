package com.jworkflow.kernel.services;
import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SingleNodeLockService implements LockService{
    
    private final List<String> locks;
    
    public SingleNodeLockService() {
        locks = new ArrayList<>();
    }

    @Override
    public synchronized boolean acquireLock(String id) {
        if (locks.contains(id))
            return false;
        
        locks.add(id);
        return true;
    }

    @Override
    public synchronized void releaseLock(String id) {
        locks.remove(id);
    }

    @Override
    public void start() {
        
    }

    @Override
    public void stop() {
        
    }
    
}
