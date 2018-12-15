package net.jworkflow.kernel.interfaces;

import java.util.function.Consumer;
import net.jworkflow.kernel.models.StepExecutionContext;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowStep;
import net.jworkflow.kernel.models.WorkflowStepInline;
import net.jworkflow.kernel.steps.ConsumerStep;

public interface WorkflowBuilder<TData> {
    void addStep(WorkflowStep step);
    int getLastStep();
    <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass);
    WorkflowDefinition build(String id, int version);
    <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass, Consumer<StepBuilder<TData, TStep>> stepSetup);
    StepBuilder<TData, WorkflowStepInline.InlineBody> startsWith(StepExecutionConsumer body);
    StepBuilder<TData, ConsumerStep> startsWith(Consumer<StepExecutionContext> body);
}
