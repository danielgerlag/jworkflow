package net.jworkflow.kernel.steps;

import java.util.function.Consumer;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public class ConsumerStep implements StepBody {

    public Consumer<StepExecutionContext> body;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) throws Exception {
        body.accept(context);
        return ExecutionResult.next();
    }
    
}
