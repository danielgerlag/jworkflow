package net.jworkflow.kernel.steps;

import java.time.Duration;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public class Delay implements StepBody {
    
    public Duration period;            
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        
        if (context.getPersistenceData() != null) {
            return ExecutionResult.next();
        }
            
        return ExecutionResult.sleep(period, true);
    }
}
