package com.jworkflow.kernel.models;

import com.google.inject.Injector;
import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.interfaces.StepFieldConsumer;
import com.jworkflow.kernel.interfaces.WorkflowHost;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WorkflowStep {
    private Class bodyType;
    private int id;
    private String name;
    private List<StepOutcome> outcomes;
    private List<StepFieldConsumer> inputs;
    private List<StepFieldConsumer> outputs;
    private ErrorBehavior retryBehavior;
    private Duration retryInterval;

    public WorkflowStep() {
        this.outcomes = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }
    
    public StepBody constructBody(Injector injector) throws InstantiationException, IllegalAccessException {
        return (StepBody)injector.getInstance(bodyType);        
        //return (StepBody)bodyType.newInstance();
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
        
    public ExecutionPipelineResult initForExecution(WorkflowHost host, PersistenceProvider persistenceStore, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer) {
        return ExecutionPipelineResult.NEXT;
    }

    public ExecutionPipelineResult beforeExecute(WorkflowHost host, PersistenceProvider persistenceStore, StepExecutionContext context, ExecutionPointer executionPointer, StepBody body) {
        return ExecutionPipelineResult.NEXT;
    }
    
    public void afterExecute(WorkflowHost host, PersistenceProvider persistenceStore, StepExecutionContext context, ExecutionResult result, ExecutionPointer executionPointer) {            
    }

    /**
     * @return the retryBehavior
     */
    public ErrorBehavior getRetryBehavior() {
        return retryBehavior;
    }

    /**
     * @param retryBehavior the retryBehavior to set
     */
    public void setRetryBehavior(ErrorBehavior retryBehavior) {
        this.retryBehavior = retryBehavior;
    }

    /**
     * @return the retryInterval
     */
    public Duration getRetryInterval() {
        return retryInterval;
    }

    /**
     * @param retryInterval the retryInterval to set
     */
    public void setRetryInterval(Duration retryInterval) {
        this.retryInterval = retryInterval;
    }
    
}
