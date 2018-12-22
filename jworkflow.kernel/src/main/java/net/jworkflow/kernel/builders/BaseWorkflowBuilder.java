package net.jworkflow.kernel.builders;
import net.jworkflow.kernel.builders.DefaultWorkflowBuilder;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowStep;
import java.util.ArrayList;
import java.util.List;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;

public class BaseWorkflowBuilder {
    
    protected List<WorkflowStep> steps;        
    
    public BaseWorkflowBuilder() {
        this.steps = new ArrayList<>();
    }
    
    public <TData> WorkflowBuilder<TData> UseData(Class<TData> dataType) {
        DefaultWorkflowBuilder<TData> result = new DefaultWorkflowBuilder<>(dataType, this.steps);
        return result;
    }
    
    public WorkflowDefinition build(String id, int version) {
        WorkflowDefinition result = new WorkflowDefinition();
        result.setId(id);
        result.setVersion(version);
        result.setSteps(steps);
        //TODO: DefaultErrorBehavior
        
        return result;
    }
    
    public void addStep(WorkflowStep step) {
        step.setId(this.steps.size());
        this.steps.add(step);
    }    
    
}


