package net.jworkflow.sample01.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class Goodbye implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Goodbye world");
        return ExecutionResult.next();
    }
    
}
