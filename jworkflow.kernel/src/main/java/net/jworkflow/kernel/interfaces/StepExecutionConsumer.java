package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;
import java.util.function.Function;

public interface StepExecutionConsumer extends Function<StepExecutionContext, ExecutionResult> {
    
}
