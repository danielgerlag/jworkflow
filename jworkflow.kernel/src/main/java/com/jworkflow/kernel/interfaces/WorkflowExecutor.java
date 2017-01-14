package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.models.*;

public interface WorkflowExecutor {
    void execute(WorkflowInstance workflow, PersistenceProvider persistenceStore, WorkflowHost host);
}
