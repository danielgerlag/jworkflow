package net.jworkflow.sample07.steps;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.*;

public class Task2 implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) throws Exception {
        System.out.println("Doing Task 2");
        throw new Exception("Explode!!!");
    }    
}
