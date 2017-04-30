package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.models.WorkflowDefinition;

public interface WorkflowRegistry {
    void registerWorkflow(Workflow workflow) throws Exception;
    WorkflowDefinition getDefinition(String workflowId, int version);
}
