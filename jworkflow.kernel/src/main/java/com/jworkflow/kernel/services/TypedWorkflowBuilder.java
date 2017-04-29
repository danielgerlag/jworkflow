package com.jworkflow.kernel.services;

import com.jworkflow.kernel.models.*;
import com.jworkflow.kernel.interfaces.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TypedWorkflowBuilder<TData> extends WorkflowBuilder {
    
    private final Class<TData> dataType;
    
    public TypedWorkflowBuilder(Class<TData> dataType, List<WorkflowStep> steps, int intitalStep) {
        super();
        this.steps = steps;
        this.dataType = dataType;
        this.setInitialStep(initialStep);
    }
    
    @Override
    public WorkflowDefinition build(String id, int version) {
        WorkflowDefinition result = super.build(id, version);        
        result.setDataType(dataType);
        return result;
    }
    
    
    public <TStep extends StepBody> StepBuilder<TData, TStep> StartsWith(Class<TStep> stepClass) {        
        return startsWith(stepClass, null);        
    }
    
    public <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass, Consumer<StepBuilder<TData, TStep>> stepSetup) {                
        WorkflowStep step = new WorkflowStep();
        step.setBodyType(stepClass);     
        step.setName(stepClass.getName());
        StepBuilder<TData, TStep> stepBuilder = new StepBuilder<>(dataType, stepClass, this, step);
                
        if (stepSetup != null)
            stepSetup.accept(stepBuilder);
        
        addStep(step);
        setInitialStep(initialStep);
        
        return stepBuilder;        
    }       
    
}