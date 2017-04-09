package com.jworkflow.kernel.models;

import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.interfaces.StepFieldConsumer;
import java.util.ArrayList;
import java.util.List;

public class WorkflowStep {
    private Class bodyType;
    private int id;
    private String name;
    private List<StepOutcome> outcomes;
    private List<StepFieldConsumer> inputs;
    private List<StepFieldConsumer> outputs;

    public WorkflowStep() {
        this.outcomes = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }
    
    public StepBody constructBody() throws InstantiationException, IllegalAccessException {
        return (StepBody)bodyType.newInstance();
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
    
    public void addOutcome(int nextStep, Object value) {
        StepOutcome outcome = new StepOutcome();
        outcome.setNextStep(nextStep);
        outcome.setValue(value);        
        outcomes.add(outcome);
    }
    
    public void addOutcome(StepOutcome outcome) {
        outcomes.add(outcome);
    }

    /**
     * @return the inputs
     */
    public List<StepFieldConsumer> getInputs() {
        return inputs;
    }

    /**
     * @param inputs the inputs to set
     */
    public void setInputs(List<StepFieldConsumer> inputs) {
        this.inputs = inputs;
    }

    /**
     * @return the outputs
     */
    public List<StepFieldConsumer> getOutputs() {
        return outputs;
    }

    /**
     * @param outputs the outputs to set
     */
    public void setOutputs(List<StepFieldConsumer> outputs) {
        this.outputs = outputs;
    }
    
}
