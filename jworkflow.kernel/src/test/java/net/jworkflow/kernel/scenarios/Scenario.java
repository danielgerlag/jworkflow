package net.jworkflow.kernel.scenarios;

import net.jworkflow.kernel.interfaces.Workflow;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.WorkflowStatus;
import net.jworkflow.kernel.services.WorkflowModule;
import net.jworkflow.kernel.interfaces.PersistenceService;

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
        PersistenceService persistence = WorkflowModule.getPersistenceProvider();
        host.registerWorkflow(workflow);
        
        host.start();
        
        String id = host.startWorkflow(WorkflowDefId, WorkflowVersion, data);
        
        WorkflowInstance instance = persistence.getWorkflowInstance(id);
        int counter = 0;
        while ((instance.getStatus() == WorkflowStatus.RUNNABLE) && (counter < 100)) {
            Thread.sleep(100);
            instance = persistence.getWorkflowInstance(id);
            counter++;
        }
                
        host.stop();
        
        return instance;
    }

    
    
}
