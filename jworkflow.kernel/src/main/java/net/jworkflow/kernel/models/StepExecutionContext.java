package net.jworkflow.kernel.models;

public class StepExecutionContext {
    private Object persistenceData;
    private WorkflowStep step;
    private WorkflowInstance workflow;

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
}
