package net.jworkflow.kernel.interfaces;

import java.time.Duration;
import java.util.Date;
import java.util.function.Function;
import net.jworkflow.kernel.models.ErrorBehavior;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.steps.*;

public interface StepBuilder<TData, TStep extends StepBody> {

    WorkflowStep getStep();

    StepBuilder<TData, TStep> input(StepFieldConsumer<TStep, TData> consumer);

    StepBuilder<TData, TStep> name(String name);

    StepBuilder<TData, TStep> onError(ErrorBehavior behavior);

    StepBuilder<TData, TStep> onError(ErrorBehavior behavior, Duration retryInterval);

    StepBuilder<TData, TStep> output(StepFieldConsumer<TStep, TData> consumer);

    /**
     * Specify the next step in the workflow
     * @param <TNewStep>
     * @param stepClass
     * @return 
     */
    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass);

    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilderConsumer stepSetup);

    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilder<TData, TNewStep> newStep);

    StepBuilder<TData, WorkflowStepInline.InlineBody> then(StepExecutionConsumer body);

    StepBuilder<TData, SubscriptionStepBody> waitFor(String eventName, Function<TData, String> eventKey, Function<TData, Date> effectiveDateUtc);

    StepBuilder<TData, SubscriptionStepBody> waitFor(String eventName, Function<TData, String> eventKey);

    StepBuilder<TData, TStep> when(Object value, WorkflowBuilderConsumer<TData> branch);
    
    ControlStepBuilder<TData, Foreach> foreach(Function<TData, Object[]> collection);
    
    ControlStepBuilder<TData, While> While(Function<TData, Boolean> condition);
    
    ControlStepBuilder<TData, If> If(Function<TData, Boolean> condition);
    
}
