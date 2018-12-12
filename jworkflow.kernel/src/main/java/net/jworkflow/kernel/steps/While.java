package net.jworkflow.kernel.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ControlStepData;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public class While implements StepBody {

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
                
                boolean complete = true;
                for (String childId: context.getExecutionPointer().children) {
                    complete = complete && context.getWorkflow().isBranchComplete(childId);
                }                       

                if (complete)
                    return ExecutionResult.persist(null);
                else
                    return ExecutionResult.persist(persistenceData);
            }
        }

        throw new Exception("Corrupt persistence data");
    }    
}
