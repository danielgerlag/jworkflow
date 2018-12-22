package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.QueueType;

public interface QueueService {
    void queueForProcessing(QueueType type, String id);
    String dequeueForProcessing(QueueType type);
    boolean isDequeueBlocking();
}
