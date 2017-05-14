package net.jworkflow.kernel.steps;

import java.util.AbstractCollection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.models.ControlStepData;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;

public class Foreach implements StepBody {

    public Function<Object, Object[]> collection;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        
        if (context.getPersistenceData() == null) {            
            Object[] resolvedCollection = collection.apply(context.getWorkflow().getData());            
            return ExecutionResult.branch(resolvedCollection, new ControlStepData(true));
        }

        if (context.getPersistenceData() instanceof ControlStepData) {

            ControlStepData persistenceData = (ControlStepData)context.getPersistenceData();                               

            if (persistenceData.childrenActive) {
                boolean complete = true;
                for (String childId: context.getExecutionPointer().children) {
                    complete = complete && isBranchComplete(context.getWorkflow().getExecutionPointers(), childId);
                }                       

                if (complete)
                    return ExecutionResult.next();
            }
        }

        return ExecutionResult.persist(context.getPersistenceData());
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
