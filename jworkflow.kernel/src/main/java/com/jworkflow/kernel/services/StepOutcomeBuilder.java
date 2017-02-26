package com.jworkflow.kernel.services;
import com.jworkflow.kernel.models.*;
import com.jworkflow.kernel.interfaces.*;
import java.util.function.Consumer;
import java.util.function.Function;


public class StepOutcomeBuilder<TData> {
     
    
    private final WorkflowBuilder workflowBuilder;
    private final StepOutcome outcome;
    private final Class<TData> dataClass;
    
    
    public StepOutcomeBuilder(Class<TData> dataClass, WorkflowBuilder workflowBuilder, StepOutcome outcome) {
        this.workflowBuilder = workflowBuilder;
        this.outcome = outcome;
        this.dataClass = dataClass;
    }
 
    
    
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass) {                
        return then(stepClass, x -> {});
    }
    
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, Consumer<StepBuilder<TData, TNewStep>> stepSetup) {                
        WorkflowStep newStep = new WorkflowStep();
        newStep.setBodyType(stepClass);        
        
        workflowBuilder.addStep(newStep);
        
        StepBuilder<TData, TNewStep> stepBuilder = new StepBuilder<>(dataClass, stepClass, workflowBuilder, newStep);
                
        if (stepSetup != null)
            stepSetup.accept(stepBuilder);
    
        outcome.setNextStep(newStep.getId());
        
        return stepBuilder;        
    }
    
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilder<TData, TNewStep> newStep) {        
        outcome.setNextStep(newStep.getStep().getId());
        StepBuilder<TData, TNewStep> stepBuilder = new StepBuilder<>(dataClass, stepClass, workflowBuilder, newStep.getStep());
        
        return stepBuilder;        
    }
    
    public StepBuilder<TData, WorkflowStepInline.InlineBody> then(Function<StepExecutionContext, ExecutionResult> body) {                
        WorkflowStepInline newStep = new WorkflowStepInline(body);        
        workflowBuilder.addStep(newStep);        
        StepBuilder<TData, WorkflowStepInline.InlineBody> stepBuilder = new StepBuilder<>(dataClass, WorkflowStepInline.InlineBody.class, workflowBuilder, newStep);        
        outcome.setNextStep(newStep.getId());
        
        return stepBuilder;        
    }
    
}
