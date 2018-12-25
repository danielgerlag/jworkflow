package net.jworkflow.sample07.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class Task3 implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Doing Task 3");
        return ExecutionResult.next();
    }    
}
