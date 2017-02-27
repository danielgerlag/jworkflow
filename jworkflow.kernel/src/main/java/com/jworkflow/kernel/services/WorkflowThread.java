package com.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.jworkflow.kernel.interfaces.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowThread implements Runnable, WorkerThread {
    
    private final WorkflowExecutor executor;
    private final QueueProvider queueProvider;
    private final LockProvider lockProvider;
    private final Logger logger;
    
    private boolean active;
        
    @Inject
    public WorkflowThread(WorkflowExecutor executor, QueueProvider queueProvider, LockProvider lockProvider, Logger logger) {
        this.executor = executor;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;        
        this.logger = logger;
        active = true;
    }

    @Override
    public void run() {
        while (active) {
            try {
                String workflowId = queueProvider.dequeueForProcessing();
                if (workflowId != null) {
                    if (lockProvider.acquireLock(workflowId)) {
                        try {
                            executor.execute(workflowId);
                        }
                        finally {
                            lockProvider.releaseLock(workflowId);
                        }
                    }
                    else {
                        logger.log(Level.INFO, String.format("Workflow %s locked", workflowId));
                    }
                }
                else {
                    Thread.sleep(500); //no work
                }
            }
            catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage());
            }
        }
    }

    
    @Override
    public boolean isActive() {
        return active;
    }

    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
}
