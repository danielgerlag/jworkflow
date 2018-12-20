package net.jworkflow.sample08.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class Hello implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Hello there");
        return ExecutionResult.next();
    }    
}
