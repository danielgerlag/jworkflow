package net.jworkflow.kernel.interfaces;

import java.util.Date;

public interface WorkflowHost {
    String startWorkflow(String workflowId, int version, Object data) throws Exception;
    void start();
    void stop();
    void registerWorkflow(Class<? extends Workflow> workflow) throws Exception;
    void registerWorkflow(Workflow workflow) throws Exception;
    void registerWorkflowFromJson(String json) throws Exception;
    void publishEvent(String eventName, String eventKey, Object eventData, Date effectiveDateUtc) throws Exception;
    
    /**
     * Suspend the given workflow, so it will not be executed until resumed
     * @param workflowId
     * @return 
     */
    boolean suspendWorkflow(String workflowId);
    
    /**
     * Resume a previously suspended workflow, so execution can continue
     * @param workflowId
     * @return 
     */
    boolean resumeWorkflow(String workflowId);
}
