package com.jworkflow.kernel.services;

import com.jworkflow.kernel.models.WorkflowDefinition;
import com.jworkflow.kernel.models.WorkflowStep;
import java.util.List;

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
            
    
}