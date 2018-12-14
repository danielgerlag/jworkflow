package net.jworkflow.kernel.services;
import net.jworkflow.kernel.steps.WaitFor;
import net.jworkflow.kernel.interfaces.StepBuilder;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.interfaces.*;
import java.time.Duration;
import java.util.AbstractCollection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.jworkflow.kernel.steps.Delay;
import net.jworkflow.kernel.steps.Foreach;
import net.jworkflow.kernel.steps.If;
import net.jworkflow.kernel.steps.Schedule;
import net.jworkflow.kernel.steps.While;

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
        WorkflowStep newStep = new WorkflowStep(stepClass);        
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
    
    //@Override
    public StepBuilder<TData, TStep> when(Object value, WorkflowBuilderConsumer<TData> branch) {
        StepOutcome result = new StepOutcome();
        result.setValue(value);
        step.addOutcome(result);        
        branch.accept(workflowBuilder);
        result.setNextStep(step.getId() + 1); //TODO: make more elegant       
        
        return this;
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
    public StepBuilder<TData, WaitFor> waitFor(String eventName, Function<TData, String> eventKey, Function<TData, Date> effectiveDateUtc) {
        WorkflowStep newStep = new WorkflowStep(WaitFor.class);
        StepFieldConsumer<WaitFor, TData> nameConsumer = (step, data) -> step.eventName = eventName;        
        StepFieldConsumer<WaitFor, TData> keyConsumer = (step, data) -> step.eventKey = eventKey.apply(data);
        StepFieldConsumer<WaitFor, TData> dateConsumer = (step, data) -> step.effectiveDate = effectiveDateUtc.apply(data);
        newStep.addInput(nameConsumer);
        newStep.addInput(keyConsumer);
        newStep.addInput(dateConsumer);
        
        workflowBuilder.addStep(newStep);        
        StepBuilder<TData, WaitFor> stepBuilder = new DefaultStepBuilder<>(dataClass, WaitFor.class, workflowBuilder, newStep);
        step.addOutcome(newStep.getId(), null);
        return stepBuilder;        
    }
    
    @Override
    public StepBuilder<TData, WaitFor> waitFor(String eventName, Function<TData, String> eventKey) {
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
        WorkflowStep newStep = new WorkflowStep(Foreach.class);
        StepFieldConsumer<Foreach, TData> collectionConsumer = (step, data) -> step.collection = collection.apply(data);                
        newStep.addInput(collectionConsumer);
        workflowBuilder.addStep(newStep);        
        ControlStepBuilder<TData, Foreach> stepBuilder = new DefaultStepBuilder<>(dataClass, Foreach.class, workflowBuilder, newStep);        
        step.addOutcome(newStep.getId(), null);
        return stepBuilder;
    }
    
    @Override
    public ControlStepBuilder<TData, While> While(Function<TData, Boolean> condition) {
        WorkflowStep newStep = new WorkflowStep(While.class);
        StepFieldConsumer<While, TData> conditionConsumer = (step, data) -> step.condition = condition.apply(data);
        newStep.addInput(conditionConsumer);
        workflowBuilder.addStep(newStep);        
        ControlStepBuilder<TData, While> stepBuilder = new DefaultStepBuilder<>(dataClass, While.class, workflowBuilder, newStep);        
        step.addOutcome(newStep.getId(), null);        
        return stepBuilder;
    }
    
    @Override
    public ControlStepBuilder<TData, If> If(Function<TData, Boolean> condition) {
        WorkflowStep newStep = new WorkflowStep(If.class);
        StepFieldConsumer<If, TData> conditionConsumer = (step, data) -> step.condition = condition.apply(data);
        newStep.addInput(conditionConsumer);
        workflowBuilder.addStep(newStep);        
        ControlStepBuilder<TData, If> stepBuilder = new DefaultStepBuilder<>(dataClass, If.class, workflowBuilder, newStep);        
        step.addOutcome(newStep.getId(), null);        
        return stepBuilder;
    }
    
    @Override
    public StepBuilder<TData, TStep> Do(WorkflowBuilderConsumer<TData> consumer) {
        consumer.accept(workflowBuilder);
        step.addChild(step.getId() + 1); //TODO: make more elegant
        return this;
    }

    @Override
    public StepBuilder<TData, Delay> delay(Function<TData, Duration> duration) {
        WorkflowStep newStep = new WorkflowStep(Delay.class);
        StepFieldConsumer<Delay, TData> durationConsumer = (step, data) -> step.duration = duration.apply(data);
        newStep.addInput(durationConsumer);
        
        workflowBuilder.addStep(newStep);        
        StepBuilder<TData, Delay> stepBuilder = new DefaultStepBuilder<>(dataClass, Delay.class, workflowBuilder, newStep);
        step.addOutcome(newStep.getId(), null);
        return stepBuilder;
    }

    @Override
    public ControlStepBuilder<TData, Schedule> schedule(Function<TData, Duration> duration) {
        WorkflowStep newStep = new WorkflowStep(Schedule.class);
        StepFieldConsumer<Schedule, TData> durationConsumer = (step, data) -> step.duration = duration.apply(data);
        newStep.addInput(durationConsumer);
        
        workflowBuilder.addStep(newStep);        
        ControlStepBuilder<TData, Schedule> stepBuilder = new DefaultStepBuilder<>(dataClass, Schedule.class, workflowBuilder, newStep);
        step.addOutcome(newStep.getId(), null);
        return stepBuilder;
    }
    
}
