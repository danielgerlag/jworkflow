package net.jworkflow.kernel.interfaces;

public interface LockService {
    boolean acquireLock(String id);
    void releaseLock(String id);
    void start();
    void stop();
}
