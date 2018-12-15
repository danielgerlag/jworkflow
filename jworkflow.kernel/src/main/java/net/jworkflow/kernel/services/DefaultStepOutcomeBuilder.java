package net.jworkflow.kernel.services;
import net.jworkflow.kernel.interfaces.StepOutcomeBuilder;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.interfaces.StepBody;
import java.util.function.Consumer;
import java.util.function.Function;
import net.jworkflow.kernel.interfaces.StepBuilder;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;


public class DefaultStepOutcomeBuilder<TData> implements StepOutcomeBuilder<TData> {
     
    
    private final WorkflowBuilder workflowBuilder;
    private final StepOutcome outcome;
    private final Class<TData> dataClass;
    
    
    public DefaultStepOutcomeBuilder(Class<TData> dataClass, WorkflowBuilder workflowBuilder, StepOutcome outcome) {
        this.workflowBuilder = workflowBuilder;
        this.outcome = outcome;
        this.dataClass = dataClass;
    }
    
    
    @Override
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass) {                
        return then(stepClass, x -> {});
    }
    
    @Override
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, Consumer<StepBuilder<TData, TNewStep>> stepSetup) {                
        WorkflowStep newStep = new WorkflowStep(stepClass);
        newStep.setName(stepClass.getName());
        
        workflowBuilder.addStep(newStep);
        
        StepBuilder<TData, TNewStep> stepBuilder = new DefaultStepBuilder<>(dataClass, stepClass, workflowBuilder, newStep);
                
        if (stepSetup != null)
            stepSetup.accept(stepBuilder);
    
        outcome.setNextStep(newStep.getId());
        
        return stepBuilder;        
    }
    
    @Override
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilder<TData, TNewStep> newStep) {        
        outcome.setNextStep(newStep.getStep().getId());
        StepBuilder<TData, TNewStep> stepBuilder = new DefaultStepBuilder<>(dataClass, stepClass, workflowBuilder, newStep.getStep());
        
        return stepBuilder;        
    }
    
    @Override
    public StepBuilder<TData, WorkflowStepInline.InlineBody> then(Function<StepExecutionContext, ExecutionResult> body) {                
        WorkflowStepInline newStep = new WorkflowStepInline(body);        
        workflowBuilder.addStep(newStep);        
        StepBuilder<TData, WorkflowStepInline.InlineBody> stepBuilder = new DefaultStepBuilder<>(dataClass, WorkflowStepInline.InlineBody.class, workflowBuilder, newStep);        
        outcome.setNextStep(newStep.getId());
        
        return stepBuilder;        
    }
    
}
