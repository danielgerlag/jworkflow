package com.jworkflow.sample02;

import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.models.*;

public class Goodbye implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Goodbye world");
        return ExecutionResult.next();
    }
    
}
