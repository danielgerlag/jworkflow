package net.jworkflow.kernel.services;

import net.jworkflow.kernel.interfaces.ParallelStepBuilder;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.interfaces.StepBuilder;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;
import net.jworkflow.kernel.interfaces.WorkflowBuilderConsumer;
import net.jworkflow.kernel.models.WorkflowStep;
import net.jworkflow.kernel.steps.Sequence;

public class DefaultParallelStepBuilder <TData, TStep extends StepBody> implements ParallelStepBuilder<TData, TStep> {

    private final WorkflowBuilder workflowBuilder;
    private final WorkflowStep step;
    private final StepBuilder<TData, Sequence> referenceBuilder;
    private final StepBuilder<TData, TStep> stepBuilder;
    
    public DefaultParallelStepBuilder(WorkflowBuilder<TData> workflowBuilder, StepBuilder<TData, TStep> stepBuilder, StepBuilder<TData, Sequence> referenceBuilder) {
        this.workflowBuilder = workflowBuilder;
        this.step = stepBuilder.getStep();
        this.referenceBuilder = referenceBuilder;
        this.stepBuilder = stepBuilder;
    }
    
    @Override
    public ParallelStepBuilder<TData, TStep> Do(WorkflowBuilderConsumer<TData> consumer) {
        int lastStep = workflowBuilder.getLastStep();
        consumer.accept(workflowBuilder);
        
        if (lastStep == workflowBuilder.getLastStep())
            throw new UnsupportedOperationException("Empty Do block not supported");

        step.addChild(lastStep + 1); //TODO: make more elegant        

        return this;
    }

    @Override
    public StepBuilder<TData, Sequence> Join() {
        return referenceBuilder;
    }
    
}
