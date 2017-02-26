package com.jworkflow.kernel.interfaces;

public interface WorkflowHost {
    String startWorkflow(String workflowId, int version, Object data) throws Exception;
}
