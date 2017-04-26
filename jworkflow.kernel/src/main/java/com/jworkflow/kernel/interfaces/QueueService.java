package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.models.QueueType;

public interface QueueService {
    void queueForProcessing(QueueType type, String id);
    String dequeueForProcessing(QueueType type);
}
