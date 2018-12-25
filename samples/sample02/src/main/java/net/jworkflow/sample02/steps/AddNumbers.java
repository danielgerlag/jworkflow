package net.jworkflow.sample02.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class AddNumbers implements StepBody {

    public int number1;
    public int number2;
    public int answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        answer = number1 + number2;
        return ExecutionResult.next();
    }    
}
