package com.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.jworkflow.kernel.interfaces.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowThread implements Runnable {
    
    private final WorkflowExecutor executor;
    private final QueueProvider queueProvider;
    private final LockProvider lockProvider;
    private final Logger logger;
    
    @Inject
    public WorkflowThread(WorkflowExecutor executor, QueueProvider queueProvider, LockProvider lockProvider, Logger logger) {
        this.executor = executor;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;        
        this.logger = logger;        
    }

    @Override
    public void run() {
        try {
            String workflowId = queueProvider.dequeueForProcessing();
            if (workflowId != null) {
                if (lockProvider.acquireLock(workflowId)) {
                    boolean requeue = false;
                    try {
                        requeue = executor.execute(workflowId);
                    }
                    finally {
                        lockProvider.releaseLock(workflowId);
                        if (requeue) {
                            logger.log(Level.INFO, String.format("Requeue workflow %s", workflowId));
                            queueProvider.queueForProcessing(workflowId);
                        }
                    }
                }
                else {
                    logger.log(Level.INFO, String.format("Workflow %s locked", workflowId));
                }
            }
        }
        catch (Exception ex) {

            logger.log(Level.SEVERE, ex.toString());
        }
    }
    
    
}
