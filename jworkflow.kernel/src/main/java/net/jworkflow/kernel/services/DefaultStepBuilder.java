package net.jworkflow.kernel.services;
import net.jworkflow.kernel.steps.SubscriptionStepBody;
import net.jworkflow.kernel.steps.SubscriptionStep;
import net.jworkflow.kernel.interfaces.StepBuilder;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.interfaces.*;
import java.time.Duration;
import java.util.AbstractCollection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.jworkflow.kernel.steps.Foreach;
import net.jworkflow.kernel.steps.ForeachStep;

public class DefaultStepBuilder<TData, TStep extends StepBody> implements StepBuilder<TData, TStep>, ControlStepBuilder<TData, TStep> {
    
    
    private final WorkflowBuilder workflowBuilder;
    private final WorkflowStep step;
    private final Class<TData> dataClass;
    
    
    public DefaultStepBuilder(Class<TData> dataClass, Class<TStep> stepClass, WorkflowBuilder workflowBuilder, WorkflowStep step) {
        this.workflowBuilder = workflowBuilder;
        this.step = step;
        this.dataClass = dataClass;
    }
    
    @Override
    public WorkflowStep getStep() {
        return step;
    }
    
    @Override
    public StepBuilder<TData, TStep> name(String name) {
        step.setName(name);
        return this;
    }
    
    
    @Override
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass) {                
        return then(stepClass, x -> {});
    }
    
    @Override
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilderConsumer stepSetup) {
        WorkflowStep newStep = new WorkflowStep();
        newStep.setBodyType(stepClass); 
        newStep.setName(stepClass.getName());
        
        workflowBuilder.addStep(newStep);
        
        StepBuilder<TData, TNewStep> stepBuilder = new DefaultStepBuilder<>(dataClass, stepClass, workflowBuilder, newStep);
                
        if (stepSetup != null)
            stepSetup.accept(stepBuilder);
        
        step.addOutcome(newStep.getId(), null);        
        
        return stepBuilder;        
    }
    
    @Override
    public <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilder<TData, TNewStep> newStep) {        
        step.addOutcome(newStep.getStep().getId(), null);
        StepBuilder<TData, TNewStep> stepBuilder = new DefaultStepBuilder<>(dataClass, stepClass, workflowBuilder, newStep.getStep());
        
        return stepBuilder;        
    }
    
    @Override
    public StepBuilder<TData, WorkflowStepInline.InlineBody> then(StepExecutionConsumer body) {                
        WorkflowStepInline newStep = new WorkflowStepInline(body);        
        workflowBuilder.addStep(newStep);
        StepBuilder<TData, WorkflowStepInline.InlineBody> stepBuilder = new DefaultStepBuilder<>(dataClass, WorkflowStepInline.InlineBody.class, workflowBuilder, newStep);        
        step.addOutcome(newStep.getId(), null);        
        
        return stepBuilder;        
    }
    
    @Override
    public StepOutcomeBuilder<TData> when(Object value) {
        StepOutcome result = new StepOutcome();
        result.setValue(value);
        step.addOutcome(result);
        StepOutcomeBuilder<TData> outcomeBuilder = new StepOutcomeBuilder<>(dataClass, workflowBuilder, result);
        
        return outcomeBuilder;
    }
    
    @Override
    public StepBuilder<TData, TStep> input(StepFieldConsumer<TStep, TData> consumer) {
        List<StepFieldConsumer> inputs = step.getInputs();        
        inputs.add(consumer);
        return this;
    }
    
    @Override
    public StepBuilder<TData, TStep> output(StepFieldConsumer<TStep, TData> consumer) {
        List<StepFieldConsumer> outputs = step.getOutputs();        
        outputs.add(consumer);
        return this;
    }
    
    @Override
    public StepBuilder<TData, SubscriptionStepBody> waitFor(String eventName, Function<TData, String> eventKey, Function<TData, Date> effectiveDateUtc) {
        SubscriptionStep newStep = new SubscriptionStep();
        newStep.eventName = eventName;
        newStep.eventKey = eventKey;
        newStep.effectiveDate = effectiveDateUtc;        
        workflowBuilder.addStep(newStep);        
        StepBuilder<TData, SubscriptionStepBody> stepBuilder = new DefaultStepBuilder<>(dataClass, SubscriptionStepBody.class, workflowBuilder, newStep);
        step.addOutcome(newStep.getId(), null);
        return stepBuilder;        
    }
    
    @Override
    public StepBuilder<TData, SubscriptionStepBody> waitFor(String eventName, Function<TData, String> eventKey) {
        return waitFor(eventName, eventKey, x -> new Date());
    }    
    
    @Override
    public StepBuilder<TData, TStep> onError(ErrorBehavior behavior) {
        step.setRetryBehavior(behavior);
        return this;
    }
    
    @Override
    public StepBuilder<TData, TStep> onError(ErrorBehavior behavior, Duration retryInterval) {
        step.setRetryBehavior(behavior);
        step.setRetryInterval(retryInterval);
        return this;
    }
    
    @Override
    public ControlStepBuilder<TData, Foreach> foreach(Function<TData, Object[]> collection) {
        ForeachStep<TData> newStep = new ForeachStep<>();
        newStep.collection = collection;
        workflowBuilder.addStep(newStep);        
        
        ControlStepBuilder<TData, Foreach> stepBuilder = new DefaultStepBuilder<>(dataClass, Foreach.class, workflowBuilder, newStep);        
        step.addOutcome(newStep.getId(), null);        
        
        return stepBuilder;
    }
    
    @Override
    public StepBuilder<TData, TStep> run(WorkflowBuilderConsumer<TData> consumer) {
        consumer.accept(workflowBuilder);
        step.addChild(step.getId() + 1); //TODO: make more elegant
        return this;
    }
    
}
