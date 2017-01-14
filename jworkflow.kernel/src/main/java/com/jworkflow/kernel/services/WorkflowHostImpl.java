package com.jworkflow.kernel.services;

import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.WorkflowHost;
import java.util.UUID;
import com.jworkflow.kernel.models.*;

@Singleton
public class WorkflowHostImpl implements WorkflowHost {

    @Override
    public String startWorkflow() {
        WorkflowInstance wf = new WorkflowInstance();        
        wf.setId(UUID.randomUUID().toString());
        
        return wf.getId();        
    }
    
}
