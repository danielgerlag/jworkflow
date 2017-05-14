package net.jworkflow.kernel.models;

public class StepExecutionContext {
    private Object persistenceData;
    private WorkflowStep step;
    private WorkflowInstance workflow;
    private Object item;
    private ExecutionPointer executionPointer;

    /**
     * @return the persistenceData
     */
    public Object getPersistenceData() {
        return persistenceData;
    }

    /**
     * @param persistenceData the persistenceData to set
     */
    public void setPersistenceData(Object persistenceData) {
        this.persistenceData = persistenceData;
    }

    /**
     * @return the step
     */
    public WorkflowStep getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(WorkflowStep step) {
        this.step = step;
    }

    /**
     * @return the workflow
     */
    public WorkflowInstance getWorkflow() {
        return workflow;
    }

    /**
     * @param workflow the workflow to set
     */
    public void setWorkflow(WorkflowInstance workflow) {
        this.workflow = workflow;
    }

    /**
     * @return the item
     */
    public Object getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(Object item) {
        this.item = item;
    }

    /**
     * @return the executionPointer
     */
    public ExecutionPointer getExecutionPointer() {
        return executionPointer;
    }

    /**
     * @param executionPointer the executionPointer to set
     */
    public void setExecutionPointer(ExecutionPointer executionPointer) {
        this.executionPointer = executionPointer;
    }
}
