package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public interface StepBody {
    ExecutionResult run(StepExecutionContext context) throws Exception;
}
