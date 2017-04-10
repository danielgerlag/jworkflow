package com.jworkflow.sample02.steps;

import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.models.*;

public class DisplayAnswer implements StepBody {

    public int Answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("The answer is " + Answer);
        return ExecutionResult.next();
    }
    
}
