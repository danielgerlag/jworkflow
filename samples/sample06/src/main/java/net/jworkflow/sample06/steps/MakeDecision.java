package net.jworkflow.sample06.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class MakeDecision implements StepBody {
        
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        return ExecutionResult.outcome(1);
    }    
}
