package net.jworkflow.sample08.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class PrintMessage implements StepBody {

    public String message;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println(message);
        return ExecutionResult.next();
    }    
}
