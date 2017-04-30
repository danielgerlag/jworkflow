package net.jworkflow.kernel.services;

import net.jworkflow.kernel.interfaces.LockService;
import net.jworkflow.kernel.interfaces.QueueService;
import net.jworkflow.kernel.interfaces.PersistenceService;
import com.google.inject.Inject;
import net.jworkflow.kernel.models.QueueType;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PollThread implements Runnable {
    
    private final PersistenceService persistence;
    private final QueueService queueProvider;
    private final LockService lockProvider;
    private final Logger logger;
           
    
    @Inject
    public PollThread(PersistenceService persistence, QueueService queueProvider, LockService lockProvider, Logger logger) {
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

