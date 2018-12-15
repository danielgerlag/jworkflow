package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.*;

public interface ExecutionResultProcessor {
    void processExecutionResult(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step, ExecutionResult result, WorkflowExecutorResult workflowResult);
    void handleStepException(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step);
}
