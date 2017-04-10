package com.jworkflow.kernel.services;
import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class SingleNodeQueueProvider implements QueueProvider{
        
    private final ConcurrentLinkedQueue<String> workflowQueue;
    private final ConcurrentLinkedQueue<String> eventQueue;
    
    public SingleNodeQueueProvider() {
        workflowQueue = new ConcurrentLinkedQueue<>();
        eventQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public synchronized void queueForProcessing(QueueType type, String id) {        
        switch (type) {
            case WORKFLOW:
                workflowQueue.add(id);
                break;
            case EVENT:
                eventQueue.add(id);
                break;
        }        
    }

    @Override
    public synchronized String dequeueForProcessing(QueueType type) {
        switch (type) {
            case WORKFLOW:
                return workflowQueue.poll();
            case EVENT:
                return eventQueue.poll();
            default:
                return null;
        }         
    }
    
}
