package net.jworkflow.kernel.steps;

import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.WorkflowStep;

public class SagaContainer extends WorkflowStep {
    
    public SagaContainer(Class bodyType) {
        super(bodyType);
    }
    
    @Override
    public boolean getResumeChildrenAfterCompensation() {
        return false;
    }
    
    @Override
    public boolean getRevertChildrenAfterCompensation() {
        return true;
    }
    
    @Override
    public void primeForRetry(ExecutionPointer pointer) {
        super.primeForRetry(pointer);
        pointer.persistenceData = null;
    }
}