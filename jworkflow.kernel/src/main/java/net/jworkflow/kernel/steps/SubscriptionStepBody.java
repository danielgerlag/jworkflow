package net.jworkflow.kernel.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public class SubscriptionStepBody implements StepBody {

    public Object eventData;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        return ExecutionResult.next();
    }
    
}
