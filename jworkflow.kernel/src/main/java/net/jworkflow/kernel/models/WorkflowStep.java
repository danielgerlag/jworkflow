package net.jworkflow.kernel.models;

import com.google.inject.Injector;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.interfaces.StepFieldConsumer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorkflowStep {
    private Class<StepBody> bodyType;
    private int id;
    private String name;
    private List<StepOutcome> outcomes;
    private List<Integer> children;
    private List<StepFieldConsumer> inputs;
    private List<StepFieldConsumer> outputs;
    private ErrorBehavior retryBehavior;
    private Duration retryInterval;
    private Integer compensationStepId;
    private String tag;

    public WorkflowStep(Class bodyType) {
        this.outcomes = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.children = new ArrayList<>();
        this.bodyType = bodyType;
    }
    
    public StepBody constructBody(Injector injector) throws InstantiationException, IllegalAccessException {
        return (StepBody)injector.getInstance(bodyType);
        //return (StepBody)bodyType.newInstance();
    }

    public Class getBodyType() {
        return bodyType;
    }

    public void setBodyType(Class bodyType) {
        this.bodyType = bodyType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StepOutcome> getOutcomes() {
        return outcomes;
    }

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

    
    public List<StepFieldConsumer> getInputs() {
        return inputs;
    }

    public void setInputs(List<StepFieldConsumer> inputs) {
        this.inputs = inputs;
    }
    
    public void addInput(StepFieldConsumer value) {
        inputs.add(value);
    }

    public List<StepFieldConsumer> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<StepFieldConsumer> outputs) {
        this.outputs = outputs;
    }
    
    public void addOutput(StepFieldConsumer value) {
        outputs.add(value);
    }
        
    public ExecutionPipelineResult initForExecution(WorkflowExecutorResult executorResult, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer) {
        return ExecutionPipelineResult.NEXT;
    }

    public ExecutionPipelineResult beforeExecute(WorkflowExecutorResult executorResult, StepExecutionContext context, ExecutionPointer executionPointer, StepBody body) {
        return ExecutionPipelineResult.NEXT;
    }
    
    public void afterExecute(WorkflowExecutorResult executorResult, StepExecutionContext context, ExecutionResult result, ExecutionPointer executionPointer) {            
    }
    
    public void primeForRetry(ExecutionPointer pointer) {
        
    }

    public ErrorBehavior getRetryBehavior() {
        return retryBehavior;
    }

    public void setRetryBehavior(ErrorBehavior retryBehavior) {
        this.retryBehavior = retryBehavior;
    }

    public Duration getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Duration retryInterval) {
        this.retryInterval = retryInterval;
    }

    public Collection<Integer> getChildren() {
        return children;
    }
    
    public void addChild(Integer child) {
        children.add(child);
    }

    public void setChildren(List<Integer> children) {
        this.children = children;
    }
    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getCompensationStepId() {
        return compensationStepId;
    }

    public void setCompensationStepId(Integer compensationStepId) {
        this.compensationStepId = compensationStepId;
    }
    
    public boolean getResumeChildrenAfterCompensation() {
        return true;
    }
    
    public boolean getRevertChildrenAfterCompensation() {
        return false;
    }

    public void afterWorkflowIteration(WorkflowExecutorResult executorResult, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer) {
    }
}