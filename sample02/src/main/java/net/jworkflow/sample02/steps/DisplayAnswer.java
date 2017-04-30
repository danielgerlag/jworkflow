package net.jworkflow.sample02.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class DisplayAnswer implements StepBody {

    public int answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("The answer is " + answer);
        return ExecutionResult.next();
    }    
}
