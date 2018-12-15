package net.jworkflow.kernel.services.errorhandlers;

import com.google.inject.Singleton;
import java.util.Queue;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.*;

@Singleton
public class SuspendHandler implements StepErrorHandler {
    
    @Override
    public ErrorBehavior getErrorBehavior() {
        return ErrorBehavior.SUSPEND;
    }

    @Override
    public void handle(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step, Queue<ExecutionPointer> bubleupQueue) {
        workflow.setStatus(WorkflowStatus.SUSPENDED);
    }
    
}
