package com.jworkflow.kernel.services;
import com.jworkflow.kernel.models.*;
import java.util.ArrayList;
import java.util.List;

public class WorkflowBuilder {
    
    protected int initialStep;    
    protected List<WorkflowStep> steps;
    
    public int getInitialStep() {
        return initialStep;
    }

    public void setInitialStep(int initialStep) {
        this.initialStep = initialStep;
    }    
    
    public WorkflowBuilder() {
        this.steps = new ArrayList<>();
    }
    
    public <TData> TypedWorkflowBuilder<TData> UseData(Class<TData> dataType) {                
        TypedWorkflowBuilder<TData> result = new TypedWorkflowBuilder<>(dataType, this.steps, this.getInitialStep());        
        return result;
    }
    
    public WorkflowDefinition build(String id, int version) {
        WorkflowDefinition result = new WorkflowDefinition();
        result.setId(id);
        result.setVersion(version);
        result.setSteps(steps);
        result.setInitialStep(initialStep);
        //TODO: DefaultErrorBehavior
        
        return result;
    }
    
    public void addStep(WorkflowStep step) {
        step.setId(this.steps.size());
        this.steps.add(step);
    }

    
    
}


