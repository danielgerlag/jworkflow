package com.jworkflow.kernel.interfaces;

public interface LockProvider {
    boolean acquireLock(String id);
    void releaseLock(String id);
    void start();
    void stop();
}
