package net.jworkflow.kernel.errorhandlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Queue;
import java.util.Stack;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.*;

@Singleton
public class CompensateHandler implements StepErrorHandler {
    
    private final ExecutionPointerFactory pointerFactory;
    private final Clock clock;
    
    @Inject
    public CompensateHandler(ExecutionPointerFactory pointerFactory, Clock clock) {
        this.pointerFactory = pointerFactory;
        this.clock = clock;
    }
    
    @Override
    public ErrorBehavior getErrorBehavior() {
        return ErrorBehavior.COMPENSATE;
    }

    @Override
    public void handle(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step, Queue<ExecutionPointer> bubleupQueue) {
        Stack<String> scope = (Stack<String>)pointer.callStack.clone();
        scope.push(pointer.id);

        while (!scope.isEmpty()) {
            String pointerId = scope.pop();
            ExecutionPointer ptr = workflow.getExecutionPointers().findById(pointerId);
            WorkflowStep ptrstep = def.findStep(ptr.stepId);
            
            boolean resume = true;
            boolean revert = false;

            Stack<String> txnStack = (Stack<String>)scope.clone();
            while (!txnStack.isEmpty()) {
                String parentId = txnStack.pop();
                ExecutionPointer parentPointer = workflow.getExecutionPointers().findById(parentId);
                WorkflowStep parentStep = def.findStep(parentPointer.stepId);
                
                if ((!parentStep.getResumeChildrenAfterCompensation()) || (parentStep.getRevertChildrenAfterCompensation())) {
                    resume = parentStep.getResumeChildrenAfterCompensation();
                    revert = parentStep.getRevertChildrenAfterCompensation();
                    break;
                }
            }

            if ((ptrstep.getRetryBehavior() != ErrorBehavior.COMPENSATE) && (ptrstep.getRetryBehavior() != null)){
                bubleupQueue.add(ptr);
                continue;            
            }
            
            pointer.active = false;
            pointer.endTimeUtc = Date.from(Instant.now(clock));
            pointer.status = PointerStatus.Failed;

            if (ptrstep.getCompensationStepId() != null) {
                ptr.status = PointerStatus.Compensated;

                ExecutionPointer compensationPointer = pointerFactory.buildCompensationPointer(def, ptr, pointer, ptrstep.getCompensationStepId());
                workflow.getExecutionPointers().add(compensationPointer);

                if (resume) {
                    step.getOutcomes().stream()
                        .filter(x -> x.getValue() == null)
                        .forEach(x -> workflow.getExecutionPointers().add(pointerFactory.buildNextPointer(def, ptr, x)));                    
                }
            }

            if (revert) {                
                workflow.getExecutionPointers().findMany(x ->  ptr.callStack.equals(x.callStack) && !x.id.equals(ptr.id) && x.status == PointerStatus.Complete)
                        .stream()
                        .sorted((x1, x2) -> x2.endTimeUtc.compareTo(x1.endTimeUtc))
                        .forEach(siblingPtr -> {
                            WorkflowStep siblingStep = def.findStep(siblingPtr.stepId);
                            if (siblingStep.getCompensationStepId() != null) {
                                ExecutionPointer compensationPointer = pointerFactory.buildCompensationPointer(def, siblingPtr, pointer, siblingStep.getCompensationStepId());
                                workflow.getExecutionPointers().add(compensationPointer);
                                siblingPtr.status = PointerStatus.Compensated;
                            }
                        });                
            }
        }
    }
    
}
