package net.jworkflow.kernel.interfaces;

public interface ControlStepBuilder<TData, TStep extends StepBody> {

    StepBuilder<TData, TStep> Do(WorkflowBuilderConsumer<TData> consumer);
    
}
