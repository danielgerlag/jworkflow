package com.jworkflow.kernel.services;
import com.jworkflow.kernel.interfaces.*;
import java.util.ArrayList;
import java.util.List;

public class SingleNodeLockProvider implements LockProvider{
    
    private final List<String> locks;
    
    public SingleNodeLockProvider() {
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
