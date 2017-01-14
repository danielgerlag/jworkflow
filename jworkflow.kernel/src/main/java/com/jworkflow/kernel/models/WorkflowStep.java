package com.jworkflow.kernel.models;

import java.util.ArrayList;
import java.util.List;

public class WorkflowStep {
    private Class bodyType;
    private int id;
    private String name;
    private List<StepOutcome> outcomes;

    public WorkflowStep() {
        this.outcomes = new ArrayList<>();
    }

    /**
     * @return the bodyType
     */
    public Class getBodyType() {
        return bodyType;
    }

    /**
     * @param bodyType the bodyType to set
     */
    public void setBodyType(Class bodyType) {
        this.bodyType = bodyType;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the outcomes
     */
    public List<StepOutcome> getOutcomes() {
        return outcomes;
    }

    /**
     * @param outcomes the outcomes to set
     */
    public void setOutcomes(List<StepOutcome> outcomes) {
        this.outcomes = outcomes;
    }
}
