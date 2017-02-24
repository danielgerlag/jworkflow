package com.jworkflow.kernel.services;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class SingleNodeQueueProvider implements QueueProvider{
        
    private final ConcurrentLinkedQueue<String> processQueue;
    
    public SingleNodeQueueProvider() {
        processQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void queueForProcessing(String id) {
        processQueue.add(id);
    }

    @Override
    public String dequeueForProcessing() {
        return processQueue.poll();
    }
    
}
