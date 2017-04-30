package net.jworkflow.kernel.models;

import net.jworkflow.kernel.interfaces.StepBody;

public class SubscriptionStepBody implements StepBody {

    public Object eventData;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        return ExecutionResult.next();
    }
    
}
