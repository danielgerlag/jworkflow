package com.jworkflow.sample02;

import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.models.*;

public class AddNumbers implements StepBody {

    public int Number1;
    public int Number2;
    public int Answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        Answer = Number1 + Number2;
        System.out.println("answer is " + Answer);
        return ExecutionResult.next();
    }
    
}
