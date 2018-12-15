package net.jworkflow.kernel.services;

import com.google.inject.Singleton;
import java.util.Stack;
import java.util.UUID;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.interfaces.ExecutionPointerFactory;

@Singleton
public class DefaultExecutionPointerFactory implements ExecutionPointerFactory {

    @Override
    public ExecutionPointer buildGenesisPointer(WorkflowDefinition def) {
        ExecutionPointer ep = new ExecutionPointer();
        ep.id = generateId();
        ep.active = true;
        ep.stepId = 0;
        ep.status = PointerStatus.Pending;
        
        return ep;
    }

    @Override
    public ExecutionPointer buildCompensationPointer(WorkflowDefinition def, ExecutionPointer pointer, ExecutionPointer exceptionPointer, int compensationStepId) {
        ExecutionPointer ep = new ExecutionPointer();
        ep.id = generateId();
        ep.predecessorId = exceptionPointer.id;
        ep.active = true;
        ep.stepId = compensationStepId;
        ep.status = PointerStatus.Pending;
        ep.contextItem = pointer.contextItem;
        ep.callStack = (Stack<String>)pointer.callStack.clone();
        
        return ep;
    }

    @Override
    public ExecutionPointer buildNextPointer(WorkflowDefinition def, ExecutionPointer pointer, StepOutcome outcomeTarget) {
        ExecutionPointer ep = new ExecutionPointer();
        ep.id = generateId();
        ep.predecessorId = pointer.id;
        ep.active = true;
        ep.stepId = outcomeTarget.getNextStep();
        ep.status = PointerStatus.Pending;
        ep.contextItem = pointer.contextItem;
        ep.callStack = (Stack<String>)pointer.callStack.clone();
        
        return ep;
    }

    @Override
    public ExecutionPointer buildChildPointer(WorkflowDefinition def, ExecutionPointer pointer, int childDefinitionId, Object branch) {
        String childPointerId = generateId();
        Stack<String> childStack = (Stack<String>)pointer.callStack.clone();
        childStack.push(pointer.id);
        pointer.children.add(childPointerId);

        ExecutionPointer ep = new ExecutionPointer();
        ep.id = childPointerId;
        ep.predecessorId = pointer.id;
        ep.active = true;
        ep.stepId = childDefinitionId;
        ep.status = PointerStatus.Pending;
        ep.contextItem = branch;
        ep.callStack = childStack;
        
        return ep;
    }
    
    private String generateId() {
        return UUID.randomUUID().toString();
    }
    
}
