package net.jworkflow.sample07.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class UndoTask3 implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Undoing Task 3");
        return ExecutionResult.next();
    }    
}
