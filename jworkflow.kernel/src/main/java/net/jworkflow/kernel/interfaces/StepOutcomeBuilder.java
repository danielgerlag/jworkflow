package net.jworkflow.kernel.interfaces;

import java.util.function.Consumer;
import java.util.function.Function;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;
import net.jworkflow.kernel.models.WorkflowStepInline;

public interface StepOutcomeBuilder<TData> {

    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass);
    
    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, Consumer<StepBuilder<TData, TNewStep>> stepSetup);

    <TNewStep extends StepBody> StepBuilder<TData, TNewStep> then(Class<TNewStep> stepClass, StepBuilder<TData, TNewStep> newStep);

    StepBuilder<TData, WorkflowStepInline.InlineBody> then(Function<StepExecutionContext, ExecutionResult> body);
    
}
