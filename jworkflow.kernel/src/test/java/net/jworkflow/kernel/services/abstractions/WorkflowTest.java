package net.jworkflow.kernel.services.abstractions;

import net.jworkflow.kernel.interfaces.PersistenceService;
import net.jworkflow.kernel.interfaces.Workflow;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.kernel.services.WorkflowModule;

public abstract class WorkflowTest {
    
    protected WorkflowHost host;
    protected PersistenceService persistence;
    
    protected abstract WorkflowModule configure();
    protected abstract Class<? extends Workflow> getWorkflow();
    
    protected void setup() throws Exception {
        WorkflowModule module = configure();
        host = WorkflowModule.getHost();
        persistence = WorkflowModule.getPersistenceProvider();
        host.registerWorkflow(getWorkflow());
        host.start();
    }
    
    protected void teardown() {
        host.stop();
    }    
    
}
