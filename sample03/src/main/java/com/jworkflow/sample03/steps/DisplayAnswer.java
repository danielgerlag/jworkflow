package com.jworkflow.sample03.steps;

import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.models.ExecutionResult;
import com.jworkflow.kernel.models.StepExecutionContext;

public class DisplayAnswer implements StepBody {

    public int answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("The answer is " + answer);
        return ExecutionResult.next();
    }
    
}