package net.jworkflow.kernel.models;

import java.time.Duration;

public class ExecutionResult {
    private boolean proceed;
    private Object outcomeValue;
    private Duration sleepFor;
    private Object persistenceData;

    public static ExecutionResult next() {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(true);
        result.setOutcomeValue(null);        
        return result;
    }
    
    public static ExecutionResult sleep(Duration duration, Object persistenceData) {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(false);
        result.setPersistenceData(persistenceData);
        result.setSleepFor(duration);
        return result;
    }
    
    /**
     * @return the proceed
     */
    public boolean isProceed() {
        return proceed;
    }

    /**
     * @param proceed the proceed to set
     */
    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }

    /**
     * @return the outcomeValue
     */
    public Object getOutcomeValue() {
        return outcomeValue;
    }

    /**
     * @param outcomeValue the outcomeValue to set
     */
    public void setOutcomeValue(Object outcomeValue) {
        this.outcomeValue = outcomeValue;
    }

    /**
     * @return the sleepFor
     */
    public Duration getSleepFor() {
        return sleepFor;
    }

    /**
     * @param sleepFor the sleepFor to set
     */
    public void setSleepFor(Duration sleepFor) {
        this.sleepFor = sleepFor;
    }

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
}
