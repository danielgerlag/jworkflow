package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.models.WorkflowDefinition;

public interface WorkflowRegistry {
    void registerWorkflow(Workflow workflow) throws Exception;
    WorkflowDefinition getDefinition(String workflowId, int version);
}
