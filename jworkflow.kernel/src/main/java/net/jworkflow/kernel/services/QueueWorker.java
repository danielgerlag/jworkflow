package net.jworkflow.kernel.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.jworkflow.kernel.interfaces.QueueService;
import net.jworkflow.kernel.models.QueueType;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.BackgroundService;

public abstract class QueueWorker implements BackgroundService {
    
    protected final QueueService queueProvider;
    protected final Logger logger;
    protected final long idleTime = 1000;    
    protected final ExecutorService workerPool;
    protected boolean active;
        
    public QueueWorker(QueueService queueProvider, Logger logger) {
        this.queueProvider = queueProvider;
        this.logger = logger;
        this.active = false;
        this.workerPool = Executors.newCachedThreadPool();
    }
    
    protected abstract int getThreadCount();
    protected abstract QueueType getQueueType();
    protected abstract void executeItem(String item) throws Exception;

    public void run() {
        while (active) {
            try {
                String item = queueProvider.dequeueForProcessing(getQueueType());

                if (item == null) {
                    if (!queueProvider.isDequeueBlocking()) {
                        Thread.sleep(idleTime);
                    }
                    continue;
                }            
                executeItem(item);
            }
            catch (Exception ex) {
                logger.log(Level.SEVERE, ex.toString());
            }
        }
    }

    @Override
    public void start() {
        active = true;
        for (int i = 0; i < getThreadCount(); i++) {
            workerPool.submit(() -> run());
        }
    }
    
    @Override
    public void stop() {
        active = false;
    }
}
