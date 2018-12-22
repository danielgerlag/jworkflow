package net.jworkflow.primitives;

import java.time.Duration;
import net.jworkflow.kernel.exceptions.CorruptPersistenceDataException;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.ScheduleStepData;
import net.jworkflow.kernel.models.StepExecutionContext;

public class Schedule implements StepBody {
    
    public Duration duration;            
    
    @Override
    public ExecutionResult run(StepExecutionContext context) throws CorruptPersistenceDataException {
        
        if (context.getPersistenceData() == null) {
            return ExecutionResult.sleep(duration, new ScheduleStepData(false));
        }

        if (context.getPersistenceData() instanceof ScheduleStepData) {
            ScheduleStepData persistenceData = (ScheduleStepData)context.getPersistenceData();                               

            if (!persistenceData.elapsed) {
                return ExecutionResult.branch(new Object[1], new ScheduleStepData(true));
            }
                
            boolean complete = true;
            for (String childId: context.getExecutionPointer().children) {
                complete = complete && context.getWorkflow().isBranchComplete(childId);
            }                       

            if (complete)
                return ExecutionResult.next();
            
            return ExecutionResult.persist(persistenceData);
        }

        throw new CorruptPersistenceDataException();
    }
}