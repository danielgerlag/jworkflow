package com.jworkflow.kernel.models;

import java.util.Date;

public class ExecutionPointer {
    private String id;
    private int stepId;
    private boolean active;
    private Date sleepUntil;
    private Object persistenceData;
    private Date startTime;
    private Date endTime;
    private String eventName;
    private String eventKey;
    private boolean eventPublished;
    private Object eventData;
    private int concurrentFork;
    private boolean pathTerminator;

    /**
     * @return the stepId
     */
    public int getStepId() {
        return stepId;
    }

    /**
     * @param stepId the stepId to set
     */
    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the sleepUntil
     */
    public Date getSleepUntil() {
        return sleepUntil;
    }

    /**
     * @param sleepUntil the sleepUntil to set
     */
    public void setSleepUntil(Date sleepUntil) {
        this.sleepUntil = sleepUntil;
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
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * @param eventName the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * @return the eventKey
     */
    public String getEventKey() {
        return eventKey;
    }

    /**
     * @param eventKey the eventKey to set
     */
    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    /**
     * @return the eventPublished
     */
    public boolean isEventPublished() {
        return eventPublished;
    }

    /**
     * @param eventPublished the eventPublished to set
     */
    public void setEventPublished(boolean eventPublished) {
        this.eventPublished = eventPublished;
    }

    /**
     * @return the eventData
     */
    public Object getEventData() {
        return eventData;
    }

    /**
     * @param eventData the eventData to set
     */
    public void setEventData(Object eventData) {
        this.eventData = eventData;
    }

    /**
     * @return the concurrentFork
     */
    public int getConcurrentFork() {
        return concurrentFork;
    }

    /**
     * @param concurrentFork the concurrentFork to set
     */
    public void setConcurrentFork(int concurrentFork) {
        this.concurrentFork = concurrentFork;
    }

    /**
     * @return the pathTerminator
     */
    public boolean isPathTerminator() {
        return pathTerminator;
    }

    /**
     * @param pathTerminator the pathTerminator to set
     */
    public void setPathTerminator(boolean pathTerminator) {
        this.pathTerminator = pathTerminator;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    
}
