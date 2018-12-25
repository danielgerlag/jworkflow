package net.jworkflow.sample08.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class AddNumbers implements StepBody {

    public int value1;
    public int value2;
    public int result;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        result = value1 + value2;
        return ExecutionResult.next();
    }    
}
