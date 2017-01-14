package com.jworkflow.kernel.interfaces;

public interface QueueProvider {
    void queueForProcessing(String id);
    String dequeueForProcessing();
}
