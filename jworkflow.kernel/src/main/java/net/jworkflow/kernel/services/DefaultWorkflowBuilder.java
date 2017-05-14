package net.jworkflow.kernel.services;

import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowStep;
import net.jworkflow.kernel.interfaces.StepBody;
import java.util.List;
import java.util.function.Consumer;
import net.jworkflow.kernel.interfaces.StepBuilder;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;

public class DefaultWorkflowBuilder<TData> extends BaseWorkflowBuilder implements WorkflowBuilder<TData> {
    
    private final Class<TData> dataType;
    
    public DefaultWorkflowBuilder(Class<TData> dataType, List<WorkflowStep> steps) {
        super();
        this.steps = steps;
        this.dataType = dataType;
    }
    
    @Override
    public WorkflowDefinition build(String id, int version) {
        WorkflowDefinition result = super.build(id, version);        
        result.setDataType(dataType);
        return result;
    }
    
    
    @Override
    public <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass) {        
        return startsWith(stepClass, null);        
    }
    
    @Override
    public <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass, Consumer<StepBuilder<TData, TStep>> stepSetup) {                
        WorkflowStep step = new WorkflowStep();
        step.setBodyType(stepClass);     
        step.setName(stepClass.getName());
        StepBuilder<TData, TStep> stepBuilder = new DefaultStepBuilder<>(dataType, stepClass, this, step);
                
        if (stepSetup != null)
            stepSetup.accept(stepBuilder);
        
        addStep(step);
        
        return stepBuilder;        
    }       
    
}