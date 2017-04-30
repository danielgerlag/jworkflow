package net.jworkflow.kernel.models;

public class StepOutcome {
    private Object value;
    private Integer nextStep;

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the nextStep
     */
    public Integer getNextStep() {
        return nextStep;
    }

    /**
     * @param nextStep the nextStep to set
     */
    public void setNextStep(Integer nextStep) {
        this.nextStep = nextStep;
    }
    
}
