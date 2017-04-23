package com.jworkflow.kernel.interfaces;

import java.util.Date;

public interface WorkflowHost {
    String startWorkflow(String workflowId, int version, Object data) throws Exception;
    void start();
    void stop();
    void registerWorkflow(Class<? extends Workflow> workflow) throws Exception;
    void registerWorkflow(Workflow workflow) throws Exception;
    void subscribeEvent(String workflowId, int stepId, String eventName, String eventKey, Date asOfUtc);
    void publishEvent(String eventName, String eventKey, Object eventData, Date effectiveDateUtc) throws Exception;
    boolean suspendWorkflow(String workflowId);
    boolean resumeWorkflow(String workflowId);
}
