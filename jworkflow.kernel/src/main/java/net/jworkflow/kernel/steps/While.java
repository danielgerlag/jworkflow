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

    public Function<Object, Boolean> condition;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) throws Exception {
        
        if (context.getPersistenceData() == null) {
            Boolean conditionResult = condition.apply(context.getWorkflow().getData());
            if (conditionResult) {
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
                    complete = complete && isBranchComplete(context.getWorkflow().getExecutionPointers(), childId);
                }                       

                if (complete)
                    return ExecutionResult.persist(null);
                else
                    return ExecutionResult.persist(persistenceData);
            }
        }

        throw new Exception("Corrupt persistence data");
    }
    
    private boolean isBranchComplete(List<ExecutionPointer> pointers, String rootId) {
        Optional<ExecutionPointer> root = pointers.stream()
                .filter(x -> x.id.equals(rootId))
                .findFirst();
        
        if (root.get().endTimeUtc == null)
            return false;

        ExecutionPointer[] list = pointers.stream()
                .filter(x -> rootId.equals(x.predecessorId))
                .toArray(ExecutionPointer[]::new);

        boolean result = true;

        for(ExecutionPointer item:  list)
            result = result && isBranchComplete(pointers, item.id);

        return result;
    }
}
