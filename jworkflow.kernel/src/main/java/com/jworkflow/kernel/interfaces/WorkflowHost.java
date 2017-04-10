package com.jworkflow.kernel.interfaces;

public interface WorkflowHost {
    String startWorkflow(String workflowId, int version, Object data) throws Exception;
    void start();
    void stop();
    void registerWorkflow(Class<? extends Workflow> workflow) throws Exception;
    void registerWorkflow(Workflow workflow) throws Exception;
    
}
