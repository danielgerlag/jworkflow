package net.jworkflow.sample08.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class UndoTask2 implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Undoing Task 2");
        return ExecutionResult.next();
    }    
}
