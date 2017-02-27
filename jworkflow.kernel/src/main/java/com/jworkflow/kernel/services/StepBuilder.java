package com.jworkflow.kernel.services;
import com.jworkflow.kernel.models.*;
import com.jworkflow.kernel.interfaces.*;

public class StepBuilder<TData, TStep extends StepBody> {
    
    
    private final WorkflowBuilder workflowBuilder;
    private final WorkflowStep step;
    private final Class<TData> dataClass;
    
    
    public StepBuilder(Class<TData> dataClass, Class<TStep> stepClass, WorkflowBuilder workflowBuilder, WorkflowStep step) {
        this.workflowBuilder = workflowBuilder;
        this.step = step;
        this.dataClass = dataClass;
    }
    
    public WorkflowStep getStep() {
        return step;
    }
    
    public StepBuilder<TData, TStep> name(String name) {
        step.setName(name);
        return this;
    }
    
    
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass) {                
        return then(stepClass, x -> {});
    }
    
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilderConsumer stepSetup) {
        WorkflowStep newStep = new WorkflowStep();
        newStep.setBodyType(stepClass); 
        newStep.setName(stepClass.getName());
        
        workflowBuilder.addStep(newStep);
        
        StepBuilder<TData, TNewStep> stepBuilder = new StepBuilder<>(dataClass, stepClass, workflowBuilder, newStep);
                
        if (stepSetup != null)
            stepSetup.accept(stepBuilder);
        
        step.addOutcome(newStep.getId(), null);        
        
        return stepBuilder;        
    }
    
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilder<TData, TNewStep> newStep) {        
        step.addOutcome(newStep.step.getId(), null);
        StepBuilder<TData, TNewStep> stepBuilder = new StepBuilder<>(dataClass, stepClass, workflowBuilder, newStep.step);
        
        return stepBuilder;        
    }
    
    public StepBuilder<TData, WorkflowStepInline.InlineBody> then(StepExecutionConsumer body) {                
        WorkflowStepInline newStep = new WorkflowStepInline(body);        
        workflowBuilder.addStep(newStep);        
        StepBuilder<TData, WorkflowStepInline.InlineBody> stepBuilder = new StepBuilder<>(dataClass, WorkflowStepInline.InlineBody.class, workflowBuilder, newStep);        
        step.addOutcome(newStep.getId(), null);        
        
        return stepBuilder;        
    }
    
    public StepOutcomeBuilder<TData> when(Object value) {
        StepOutcome result = new StepOutcome();
        result.setValue(value);
        step.addOutcome(result);
        StepOutcomeBuilder<TData> outcomeBuilder = new StepOutcomeBuilder<>(dataClass, workflowBuilder, result);
        
        return outcomeBuilder;
    }
    
    
}
