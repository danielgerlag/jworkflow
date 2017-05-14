package net.jworkflow.kernel.interfaces;

import java.util.function.Consumer;


public interface ControlStepBuilder<TData, TStep extends StepBody> {

    StepBuilder<TData, TStep> run(WorkflowBuilderConsumer<TData> consumer);
    
}
