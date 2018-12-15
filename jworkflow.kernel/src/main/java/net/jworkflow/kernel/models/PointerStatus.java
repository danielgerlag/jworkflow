package net.jworkflow.kernel.models;

public enum PointerStatus {
    
    Pending(1),
    Running(2),
    Complete(3),
    Sleeping(4),
    WaitingForEvent(5),
    Failed(6),
    Compensated(7);
    
    private final int value; 
  
    public int getValue() { 
        return this.value; 
    } 
  
    private PointerStatus(int value) { 
        this.value = value; 
    } 
}
