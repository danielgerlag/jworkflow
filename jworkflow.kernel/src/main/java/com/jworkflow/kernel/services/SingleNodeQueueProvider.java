package com.jworkflow.kernel.services;
import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class SingleNodeQueueProvider implements QueueProvider{
        
    private final ConcurrentLinkedQueue<String> processQueue;
    
    public SingleNodeQueueProvider() {
        processQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public synchronized void queueForProcessing(String id) {
        processQueue.add(id);
    }

    @Override
    public synchronized String dequeueForProcessing() {
        return processQueue.poll();
    }
    
}
