package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.models.ExecutionResult;
import com.jworkflow.kernel.models.StepExecutionContext;

public interface StepBody {
    ExecutionResult run(StepExecutionContext context);
}
