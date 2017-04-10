package com.jworkflow.kernel.scenarios;

import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.interfaces.Workflow;
import com.jworkflow.kernel.interfaces.WorkflowHost;
import com.jworkflow.kernel.models.WorkflowInstance;
import com.jworkflow.kernel.models.WorkflowStatus;
import com.jworkflow.kernel.services.WorkflowModule;

public abstract class Scenario {

    protected String WorkflowDefId;
    protected int WorkflowVersion;

    public Scenario() {
        this.WorkflowDefId = "scenario";
        this.WorkflowVersion = 1;
    }
    
    protected void setupWorkflow() {
        WorkflowModule.setup();
    }
        
    protected WorkflowInstance runWorkflow(Workflow workflow, Object data) throws Exception{
        setupWorkflow();
        WorkflowHost host = WorkflowModule.getHost();
        PersistenceProvider persistence = WorkflowModule.getPersistenceProvider();
        host.registerWorkflow(workflow);
        
        host.start();
        
        String id = host.startWorkflow(WorkflowDefId, WorkflowVersion, data);
        
        WorkflowInstance instance = persistence.getWorkflowInstance(id);
        int counter = 0;
        while ((instance.getStatus() == WorkflowStatus.RUNNABLE) && (counter < 60)) {
            Thread.sleep(100);
            instance = persistence.getWorkflowInstance(id);
            counter++;
        }
                
        host.stop();
        
        return instance;
    }

    
    
}
