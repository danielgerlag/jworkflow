package net.jworkflow.kernel.steps;

import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.PointerStatus;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowExecutorResult;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.WorkflowStep;

public class CancellableStep<TData> extends WorkflowStep {
    
    private final Function<TData, Boolean> condition;
    
    public CancellableStep(Class bodyType, Function<TData, Boolean> condition) {
        super(bodyType);
        this.condition = condition;
    }
    
    @Override
    public void afterWorkflowIteration(WorkflowExecutorResult executorResult, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer) {
        Boolean result = condition.apply((TData)workflow.getData());
        if (result) {
            executionPointer.active = false;
            executionPointer.status = PointerStatus.Complete;
            executionPointer.endTimeUtc = Date.from(Instant.now());
        }
    }
}
