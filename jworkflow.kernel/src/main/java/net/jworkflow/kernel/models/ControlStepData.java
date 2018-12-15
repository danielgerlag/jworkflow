package net.jworkflow.kernel.models;

public class ControlStepData {
    
    public boolean childrenActive;
    
    public ControlStepData() {        
    }
    
    public ControlStepData(boolean childrenActive) {
        this.childrenActive = childrenActive;
    }    
}
