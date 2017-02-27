package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.models.ExecutionResult;
import com.jworkflow.kernel.models.StepExecutionContext;
import java.util.function.Function;

public interface StepExecutionConsumer extends Function<StepExecutionContext, ExecutionResult> {
    
}
