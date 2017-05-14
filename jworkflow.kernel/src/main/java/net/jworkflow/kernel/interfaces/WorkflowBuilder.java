package net.jworkflow.kernel.interfaces;

import java.util.function.Consumer;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowStep;

public interface WorkflowBuilder<TData> {
    void addStep(WorkflowStep step);
    <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass);
    WorkflowDefinition build(String id, int version);
    <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass, Consumer<StepBuilder<TData, TStep>> stepSetup);    
}
