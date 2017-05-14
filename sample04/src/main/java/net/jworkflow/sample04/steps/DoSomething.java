package net.jworkflow.sample04.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class DoSomething implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Doing something with " + context.getItem());
        return ExecutionResult.next();
    }    
}
