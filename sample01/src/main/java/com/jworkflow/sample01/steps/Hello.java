package com.jworkflow.sample01.steps;

import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.models.*;

public class Hello implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Hello world");
        return ExecutionResult.next();
    }    
}
