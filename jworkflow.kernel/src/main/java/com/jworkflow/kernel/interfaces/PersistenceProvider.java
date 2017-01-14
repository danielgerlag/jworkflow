package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.models.*;

public interface PersistenceProvider {
    String createNewWorkflow(WorkflowInstance workflow);
    void persistWorkflow(WorkflowInstance workflow);
    Iterable<String> getRunnableInstances();
    WorkflowInstance GetWorkflowInstance(String id);
}
