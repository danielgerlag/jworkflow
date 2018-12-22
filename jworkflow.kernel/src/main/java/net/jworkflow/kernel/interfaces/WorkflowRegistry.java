package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.WorkflowDefinition;

public interface WorkflowRegistry {
    void registerWorkflow(Workflow workflow) throws Exception;
    void registerWorkflow(WorkflowDefinition definition) throws Exception;
    WorkflowDefinition getDefinition(String workflowId, int version);
}
