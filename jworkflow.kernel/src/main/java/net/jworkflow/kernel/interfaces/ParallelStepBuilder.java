package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.steps.Sequence;

public interface ParallelStepBuilder<TData, TStep extends StepBody> {
    ParallelStepBuilder<TData, TStep> Do(WorkflowBuilderConsumer<TData> consumer);
    StepBuilder<TData, Sequence> Join();
}
