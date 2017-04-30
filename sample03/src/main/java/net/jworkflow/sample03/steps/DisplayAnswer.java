package net.jworkflow.sample03.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public class DisplayAnswer implements StepBody {

    public Object answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("The answer is " + answer);
        return ExecutionResult.next();
    }
    
}