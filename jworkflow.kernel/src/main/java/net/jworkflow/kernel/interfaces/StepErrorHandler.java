package net.jworkflow.kernel.interfaces;

import java.util.Queue;
import net.jworkflow.kernel.models.*;

public interface StepErrorHandler {
    ErrorBehavior getErrorBehavior();
    void handle(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step, Queue<ExecutionPointer> bubleupQueue);
}
