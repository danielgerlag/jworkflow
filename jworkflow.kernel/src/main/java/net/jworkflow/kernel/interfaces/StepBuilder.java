package net.jworkflow.kernel.interfaces;

import java.time.Duration;
import java.util.Date;
import java.util.function.Function;
import net.jworkflow.kernel.models.ErrorBehavior;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.steps.*;

public interface StepBuilder<TData, TStep extends StepBody> {

    WorkflowStep getStep();

    /**
     * Map properties on the step to properties on the workflow data object before the step executes
     * @param consumer
     * A consumer the injects the workflow data and step objects as parameters
     * {@literal (step, data) -> ...}
     * 
     * map your step properties to workflow data, eg.
     * {@literal (step, data) -> step.number1 = data.value1}
     * @return 
     */
    StepBuilder<TData, TStep> input(StepFieldConsumer<TStep, TData> consumer);

    /**
     * Specifies a display name for the step
     * @param name
     * A display name for the step for easy identification in logs, etc...
     * @return 
     */
    StepBuilder<TData, TStep> name(String name);

    /**
     * Specify the behavior should this step throw an exception
     * @param behavior
     * @return 
     */
    StepBuilder<TData, TStep> onError(ErrorBehavior behavior);

    /**
     * Specify the behavior should this step throw an exception
     * @param behavior
     * @param retryInterval
     * @return 
     */
    StepBuilder<TData, TStep> onError(ErrorBehavior behavior, Duration retryInterval);

    /**
     * Map properties on the workflow data object to properties on the step after the step executes
     * @param consumer
     * A consumer the injects the workflow data and step objects as parameters
     * {@literal (step, data) -> ...}
     * 
     * map your step properties to workflow data, eg.
     * {@literal (step, data) -> step.value1 = data.value1}
     * @return 
     */
    StepBuilder<TData, TStep> output(StepFieldConsumer<TStep, TData> consumer);

    /**
     * Specify the next step in the workflow
     * @param <TNewStep>
     * The class of the next step
     * @param stepClass
     * The class of the next step
     * @return 
     */
    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass);

    /**
     * Specify the next step in the workflow
     * @param <TNewStep>
     * @param stepClass
     * @param stepSetup
     * @return 
     */
    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilderConsumer stepSetup);

    /**
     * Specify the next step in the workflow
     * @param <TNewStep>
     * @param stepClass
     * @param newStep
     * @return 
     */
    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilder<TData, TNewStep> newStep);

    /**
     * Specify the next step in the workflow
     * @param body
     * @return 
     */
    StepBuilder<TData, WorkflowStepInline.InlineBody> then(StepExecutionConsumer body);

    /**
     * Put the workflow to sleep until to specified event is published
     * @param eventName
     * @param eventKey
     * @param effectiveDateUtc
     * @return 
     */
    StepBuilder<TData, WaitFor> waitFor(String eventName, Function<TData, String> eventKey, Function<TData, Date> effectiveDateUtc);

    /**
     * Put the workflow to sleep until to specified event is published
     * @param eventName
     * @param eventKey
     * @return 
     */
    StepBuilder<TData, WaitFor> waitFor(String eventName, Function<TData, String> eventKey);

    //StepBuilder<TData, TStep> when(Object value, WorkflowBuilderConsumer<TData> branch);
    
    /**
     * Start a parallel foreach based on the given function which evaluates to an array.
     * For example:
     * {@literal .foreach(data -> new String[] { "item 1", "item 2", "item 3" })
            .Do(each -> each
                .startsWith(DoSomething.class))
        .then(Goodbye.class);}
     * @param collection
     * The function that is evaluated to return the collection when the workflow reaches this point
     * @return 
     */
    ControlStepBuilder<TData, Foreach> foreach(Function<TData, Object[]> collection);
    
    /**
     * Start a while loop that evaluates the given expression on each iteration
     * @param condition
     * @return 
     */
    ControlStepBuilder<TData, While> While(Function<TData, Boolean> condition);
    
    /**
     * Only execute a given set of steps based on the evaluation of the expression
     * @param condition
     * @return 
     */
    ControlStepBuilder<TData, If> If(Function<TData, Boolean> condition);
    
    /**
     * Put the this workflow branch to sleep for a specified time
     * @param duration
     * @return 
     */
    StepBuilder<TData, Delay> delay(Function<TData, Duration> duration);
    
    /**
     * Schedules the future execution of a branch of steps
     * @param duration
     * @return 
     */
    ControlStepBuilder<TData, Schedule> schedule(Function<TData, Duration> duration);
    
}
