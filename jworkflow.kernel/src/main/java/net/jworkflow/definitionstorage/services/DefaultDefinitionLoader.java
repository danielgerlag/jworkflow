package net.jworkflow.definitionstorage.services;

import com.google.gson.Gson;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import net.jworkflow.definitionstorage.models.*;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.interfaces.WorkflowRegistry;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.steps.CancellableStep;
import net.jworkflow.kernel.steps.SagaContainer;

public class DefaultDefinitionLoader implements DefinitionLoader {

    private final WorkflowRegistry registry;
    private final ScriptEngine scriptEngine;
    private final Gson gson;
    
    @Inject
    public DefaultDefinitionLoader(WorkflowRegistry registry, ScriptEngine scriptEngine) {
        this.registry = registry;
        this.scriptEngine = scriptEngine;
        this.gson = new Gson();
    }

    @Override
    public WorkflowDefinition loadDefinition(String json) throws Exception {
        DefinitionSource source = gson.fromJson(json, DefinitionSource.class);
        WorkflowDefinition def = convert(source);
        registry.registerWorkflow(def);
        return def;
    }
    
    private WorkflowDefinition convert(DefinitionSource source) throws WorkflowDefinitionLoadException, ClassNotFoundException {
        Class dataType = Object.class;
        
        if (source.dataType != null)
            dataType = findType(source.dataType);

        WorkflowDefinition result = new WorkflowDefinition();
        
        result.setId(source.id);
        result.setVersion(source.version);
        result.setSteps(convertSteps(source.steps, dataType));
        result.setDescription(source.description);
        result.setDataType(dataType);
        
        return result;
    }
    
    private List<WorkflowStep> convertSteps(Collection<StepSource> source, Class dataType) throws WorkflowDefinitionLoadException, ClassNotFoundException {
        List<WorkflowStep> result = new ArrayList<>();
        int i = 0;                
        
        Stack<StepSource> stack = new Stack<>();
        stack.addAll(reverse(source));
        
        List<StepSource> parents = new ArrayList<>();
        List<StepSource> compensatables = new ArrayList<>();

        while (!stack.isEmpty()) {
            StepSource nextStep = stack.pop();

            Class stepType = findType(nextStep.stepType);            
            WorkflowStep targetStep = new WorkflowStep(stepType);

            if (nextStep.cancelCondition != null) {                
                targetStep = new CancellableStep(stepType, (data) -> {
                    Bindings bindings = scriptEngine.createBindings();
                    bindings.put("data", data);
                    try {
                        return (Boolean)scriptEngine.eval(nextStep.cancelCondition, bindings);
                    } catch (ScriptException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }

            if (nextStep.saga) {
                targetStep = new SagaContainer(stepType);
            }

            targetStep.setId(i);
            targetStep.setName(nextStep.name);
            targetStep.setRetryBehavior(nextStep.errorBehavior);
            targetStep.setRetryInterval(nextStep.retryInterval);
            targetStep.setTag(nextStep.id);

            attachInputs(nextStep, dataType, stepType, targetStep);
            attachOutputs(nextStep, dataType, stepType, targetStep);

            if (nextStep.Do != null) {
                for (List<StepSource> branch: nextStep.Do) {
                    reverse(branch).stream().forEach((child) -> {
                        stack.push(child);
                    });
                }

                if (!nextStep.Do.isEmpty())
                    parents.add(nextStep);
            }

            if (nextStep.compensateWith != null) {
                reverse(nextStep.compensateWith).stream().forEach((compChild) -> {
                    stack.push(compChild);
                });

                if (!nextStep.compensateWith.isEmpty())
                    compensatables.add(nextStep);
            }

            if (nextStep.nextStepId != null) {
                StepOutcome outcome = new StepOutcome();
                outcome.setTag(nextStep.nextStepId);
                targetStep.addOutcome(outcome); 
            }            

            result.add(targetStep);
            i++;
        }

        for (WorkflowStep step: result) {
            if (result.stream().anyMatch(x -> (x.getTag() == null ? step.getTag() == null : x.getTag().equals(step.getTag())) && x.getId() != step.getId())) {
                throw new WorkflowDefinitionLoadException("Duplicate step Id " + step.getTag());
            }

            for (StepOutcome outcome: step.getOutcomes()) {
                if (result.stream().allMatch(x -> (x.getTag() == null ? outcome.getTag() != null : !x.getTag().equals(outcome.getTag()))))
                    throw new WorkflowDefinitionLoadException("Cannot find step id " + outcome.getTag());                

                WorkflowStep next = result.stream().filter(x -> (x.getTag() == null ? outcome.getTag() == null : x.getTag().equals(outcome.getTag()))).findFirst().get();
                outcome.setNextStep(next.getId());
            }
        }

        for (StepSource parent: parents) {
            WorkflowStep target = result.stream().filter(x -> x.getTag() == parent.id).findFirst().get();
            for (List<StepSource> branch: parent.Do) {
                
                List<String> childTags = branch
                    .stream()
                    .map(x -> x.id)
                    .collect(Collectors.toList());
                
                result
                    .stream()
                    .filter(x -> childTags.contains(x.getTag()))
                    .map(WorkflowStep::getId)
                    .sorted(Integer::compare)
                    .limit(1)
                    .forEach(x -> target.addChild(x));
            }
        }

        compensatables.stream().forEach((item) -> {
            WorkflowStep target = result.stream().filter(x -> x.getTag() == item.id).findFirst().get();
            Optional<String> tag = item.compensateWith.stream().map(x -> x.id).findFirst();
            if (tag.isPresent()) {
                Optional<WorkflowStep> compStep = result.stream().filter(x -> x.getTag() == tag.get()).findFirst();
                if (compStep.isPresent()) {
                    target.setCompensationStepId(compStep.get().getId());
                }
            }
        });

        return result;
    }
    
    private void attachInputs(StepSource source, Class dataType, Class<StepBody> stepType, WorkflowStep workflowStep) {
        for (String field: source.inputs.stringPropertyNames()) {
            String expression = source.inputs.getProperty(field);
            workflowStep.addInput((step, data) -> {
                Bindings bindings = scriptEngine.createBindings();
                bindings.put("step", step);
                bindings.put("data", data);
                try {                    
                    Object value = scriptEngine.eval(expression, bindings);
                    stepType.getField(field).set(step, value);
                } catch (ScriptException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }                
            });
        }        
    }
    
    private void attachOutputs(StepSource source, Class dataType, Class<StepBody> stepType, WorkflowStep workflowStep) {
        for (String field: source.outputs.stringPropertyNames()) {
            String expression = source.outputs.getProperty(field);
            workflowStep.addOutput((step, data) -> {
                Bindings bindings = scriptEngine.createBindings();
                bindings.put("step", step);
                bindings.put("data", data);
                try {                    
                    Object value = scriptEngine.eval(expression, bindings);
                    dataType.getField(field).set(data, value);
                } catch (ScriptException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }                
            });
        }        
    }
    
    private <T> List<T> reverse(Collection<T> collection) {
        List<T> result = new ArrayList<>(collection);
        Collections.reverse(result);
        return result;
    }
    
    private Class findType(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

}
