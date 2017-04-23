package com.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.QueueType;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PollThread implements Runnable {
    
    private final PersistenceProvider persistence;
    private final QueueProvider queueProvider;
    private final LockProvider lockProvider;
    private final Logger logger;
           
    
    @Inject
    public PollThread(PersistenceProvider persistence, QueueProvider queueProvider, LockProvider lockProvider, Logger logger) {
        this.persistence = persistence;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;        
        this.logger = logger;        
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Polling for runnables");
        try {
            if (lockProvider.acquireLock("poll-workflows")) {
                try {
                    Iterable<String> runnables = persistence.getRunnableInstances();
                    runnables.forEach(item -> {
                       queueProvider.queueForProcessing(QueueType.WORKFLOW, item);
                    });            
                }
                finally {
                    lockProvider.releaseLock("poll-workflows");
                }
            }            
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }        
        
        try {
            if (lockProvider.acquireLock("poll-events")) {
                try {
                    Iterable<String> runnables = persistence.getRunnableEvents();
                    runnables.forEach(item -> {
                       queueProvider.queueForProcessing(QueueType.EVENT, item);
                    });            
                }
                finally {
                    lockProvider.releaseLock("poll-events");
                }
            }            
        }
        catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }        
    }

    
}

