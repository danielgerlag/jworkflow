package net.jworkflow.kernel.services;

import net.jworkflow.kernel.interfaces.WorkflowExecutor;
import net.jworkflow.kernel.interfaces.LockService;
import net.jworkflow.kernel.interfaces.QueueService;
import com.google.inject.Inject;
import net.jworkflow.kernel.models.QueueType;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkflowThread implements Runnable {
    
    private final WorkflowExecutor executor;
    private final QueueService queueProvider;
    private final LockService lockProvider;
    private final Logger logger;
    
    @Inject
    public WorkflowThread(WorkflowExecutor executor, QueueService queueProvider, LockService lockProvider, Logger logger) {
        this.executor = executor;
        this.queueProvider = queueProvider;
        this.lockProvider = lockProvider;        
        this.logger = logger;        
    }

    @Override
    public void run() {
        try {
            String workflowId = queueProvider.dequeueForProcessing(QueueType.WORKFLOW);
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
                            queueProvider.queueForProcessing(QueueType.WORKFLOW, workflowId);
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
