package net.jworkflow.primitives;

import net.jworkflow.kernel.exceptions.CorruptPersistenceDataException;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ControlStepData;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public class If implements StepBody {

    public boolean condition;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) throws Exception {
        
        if (context.getPersistenceData() == null) {
            if (condition) {
                Object[] defaultList = new Object[]{null};
                return ExecutionResult.branch(defaultList, new ControlStepData(true));
            }
            else
                return ExecutionResult.next();
        }

        if (context.getPersistenceData() instanceof ControlStepData) {
            ControlStepData persistenceData = (ControlStepData)context.getPersistenceData();                               

            if (persistenceData.childrenActive) {
                if (context.getWorkflow().isBranchComplete(context.getExecutionPointer().id))
                    return ExecutionResult.next();
                else
                    return ExecutionResult.persist(persistenceData);
            }
        }

        throw new CorruptPersistenceDataException();
    }
}
