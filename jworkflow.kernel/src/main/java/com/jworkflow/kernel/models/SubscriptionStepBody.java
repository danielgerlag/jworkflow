package com.jworkflow.kernel.models;

import com.jworkflow.kernel.interfaces.StepBody;

public class SubscriptionStepBody implements StepBody {

    public Object eventData;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        return ExecutionResult.next();
    }
    
}
