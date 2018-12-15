package net.jworkflow.kernel.models;

import java.time.Duration;
import java.util.Date;

public class ExecutionResult {
    private boolean proceed;
    private Object outcomeValue;
    private Duration sleepFor;
    private Object persistenceData;
    private Object[] branches = new Object[]{}; 
    private String eventName;
    private String eventKey;
    private Date eventAsOf;
            

    public static ExecutionResult next() {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(true);
        result.setOutcomeValue(null);        
        return result;
    }
    
    public static ExecutionResult outcome(Object value) {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(true);
        result.setOutcomeValue(value);
        return result;
    }
    
    public static ExecutionResult sleep(Duration duration, Object persistenceData) {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(false);
        result.setPersistenceData(persistenceData);
        result.setSleepFor(duration);
        return result;
    }
    
    public static ExecutionResult persist(Object persistenceData) {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(false);
        result.setPersistenceData(persistenceData);
        return result;
    }
    
    public static ExecutionResult branch(Object[] branches, Object persistenceData) {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(false);
        result.setPersistenceData(persistenceData);
        result.setBranches(branches);
        return result;
    }
    
    public static ExecutionResult waitForEvent(String eventName, String eventKey, Date effectiveDate) {
        ExecutionResult result = new ExecutionResult();
        result.setProceed(false);
        result.setOutcomeValue(null);
        result.setEventName(eventName);
        result.setEventKey(eventKey);
        result.setEventAsOf(effectiveDate);
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

    /**
     * @return the branches
     */
    public Object[] getBranches() {
        return branches;
    }

    /**
     * @param branches the branches to set
     */
    public void setBranches(Object[] branches) {
        this.branches = branches;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public Date getEventAsOf() {
        return eventAsOf;
    }

    public void setEventAsOf(Date eventAsOf) {
        this.eventAsOf = eventAsOf;
    }
}
