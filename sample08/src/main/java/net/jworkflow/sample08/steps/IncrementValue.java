package net.jworkflow.sample08.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class IncrementValue implements StepBody {

    public int value;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Incrementing...");
        value++;
        return ExecutionResult.next();
    }    
}
