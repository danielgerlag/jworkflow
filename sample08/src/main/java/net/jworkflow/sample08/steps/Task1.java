package net.jworkflow.sample08.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class Task1 implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Doing Task 1");
        return ExecutionResult.next();
    }    
}
