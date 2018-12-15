package net.jworkflow.kernel.services;

import java.util.Comparator;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowStep;
import net.jworkflow.kernel.interfaces.StepBody;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import net.jworkflow.kernel.interfaces.StepBuilder;
import net.jworkflow.kernel.interfaces.StepExecutionConsumer;
import net.jworkflow.kernel.interfaces.StepFieldConsumer;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;
import net.jworkflow.kernel.models.StepExecutionContext;
import net.jworkflow.kernel.models.WorkflowStepInline;
import net.jworkflow.kernel.steps.ConsumerStep;

public class DefaultWorkflowBuilder<TData> extends BaseWorkflowBuilder implements WorkflowBuilder<TData> {
    
    private final Class<TData> dataType;
    
    public DefaultWorkflowBuilder(Class<TData> dataType, List<WorkflowStep> steps) {
        super();
        this.steps = steps;
        this.dataType = dataType;
    }
    
    @Override
    public WorkflowDefinition build(String id, int version) {
        WorkflowDefinition result = super.build(id, version);        
        result.setDataType(dataType);
        return result;
    }

    @Override
    public <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass) {        
        return startsWith(stepClass, null);        
    }
    
    @Override
    public <TStep extends StepBody> StepBuilder<TData, TStep> startsWith(Class<TStep> stepClass, Consumer<StepBuilder<TData, TStep>> stepSetup) {                
        WorkflowStep step = new WorkflowStep(stepClass);
        step.setName(stepClass.getName());
        StepBuilder<TData, TStep> stepBuilder = new DefaultStepBuilder<>(dataType, stepClass, this, step);
                
        if (stepSetup != null)
            stepSetup.accept(stepBuilder);
        
        addStep(step);
        
        return stepBuilder;
    }
    
    @Override
    public int getLastStep() {
        return steps.stream()
                .max(Comparator.comparing(WorkflowStep::getId))
                .orElseThrow(NoSuchElementException::new)
                .getId();        
    }
    
    @Override
    public StepBuilder<TData, WorkflowStepInline.InlineBody> startsWith(StepExecutionConsumer body) {
        WorkflowStepInline step = new WorkflowStepInline(body);        
        addStep(step);
        StepBuilder<TData, WorkflowStepInline.InlineBody> stepBuilder = new DefaultStepBuilder<>(dataType, WorkflowStepInline.InlineBody.class, this, step);

        return stepBuilder;        
    }

    @Override
    public StepBuilder<TData, ConsumerStep> startsWith(Consumer<StepExecutionContext> body) {
        WorkflowStep newStep = new WorkflowStep(ConsumerStep.class);
        StepFieldConsumer<ConsumerStep, TData> bodyConsumer = (step, data) -> step.body = body;
        newStep.addInput(bodyConsumer);        
        StepBuilder<TData, ConsumerStep> stepBuilder = new DefaultStepBuilder<>(dataType, ConsumerStep.class, this, newStep);
        addStep(newStep);
        
        return stepBuilder;
    }
}