package com.jworkflow.kernel.interfaces;
import com.jworkflow.kernel.models.*;


public interface WorkflowBuilder {
    int getInitialStep();
    void setInitialStep(int value);
    <TData> TypedWorkflowBuilder<TData> UseData();    
    WorkflowDefinition build(String id, int version);    
    void addStep(WorkflowStep step);
}


