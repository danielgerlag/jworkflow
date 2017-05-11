package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.WorkflowExecutorResult;
import net.jworkflow.kernel.models.WorkflowInstance;

public interface WorkflowExecutor {
    WorkflowExecutorResult execute(WorkflowInstance workflow);
}
