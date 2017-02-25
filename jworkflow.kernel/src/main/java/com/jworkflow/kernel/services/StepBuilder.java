package com.jworkflow.kernel.services;
import com.jworkflow.kernel.models.*;
import com.jworkflow.kernel.interfaces.*;


public class StepBuilder<TData, TStep extends StepBody> {
     
    
    private final WorkflowBuilder workflowBuilder;
    private final WorkflowStep step;
    
    
    public StepBuilder(Class<TData> dataClass, Class<TStep> stepClass, WorkflowBuilder workflowBuilder, WorkflowStep step) {
        this.workflowBuilder = workflowBuilder;
        this.step = step;
    }
    
    
    public void doSomthing() {
        
    }
    
    
}
