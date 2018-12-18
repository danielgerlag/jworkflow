package net.jworkflow.kernel.services.abstractions;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Date;
import net.jworkflow.kernel.interfaces.PersistenceService;
import net.jworkflow.kernel.interfaces.Workflow;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.WorkflowStatus;
import net.jworkflow.kernel.services.WorkflowModule;

public abstract class WorkflowTest<TData> {
    
    protected WorkflowHost host;
    protected PersistenceService persistence;
    
    //protected abstract WorkflowModule configure();
    protected abstract Workflow getWorkflow();
    
    protected void setup() throws Exception {
        //WorkflowModule module = configure();
        WorkflowModule.setup();
        host = WorkflowModule.getHost();
        persistence = WorkflowModule.getPersistenceProvider();
        host.registerWorkflow(getWorkflow());
        host.start();
    }
    
    protected String startWorkflow(Object data) throws Exception {
        return host.startWorkflow(getWorkflow().getId(), getWorkflow().getVersion(), data);
    }
    
    protected void waitForWorkflowToComplete(String workflowId) throws InterruptedException {
        WorkflowInstance instance = persistence.getWorkflowInstance(workflowId);
        int counter = 0;
        while ((instance.getStatus() == WorkflowStatus.RUNNABLE) && (counter < 100)) {
            Thread.sleep(100);
            instance = persistence.getWorkflowInstance(workflowId);
            counter++;
        }
    }
    
    protected void waitForEventSubscription(String eventName, String eventKey) throws InterruptedException {
        int counter = 0;
        while ((getActiveSubscriptons(eventName, eventKey).isEmpty()) && (counter < 100)) {
            Thread.sleep(100);
            counter++;
        }
    }
    
    protected WorkflowStatus getStatus(String workflowId) {
        WorkflowInstance instance = persistence.getWorkflowInstance(workflowId);
        return instance.getStatus();
    }

    protected TData GetData(String workflowId)
    {
        WorkflowInstance instance = persistence.getWorkflowInstance(workflowId);
        return (TData)instance.getData();
    }
    
    protected Collection<EventSubscription> getActiveSubscriptons(String eventName, String eventKey) {
        Date effectiveDate = Date.from(Instant.now());
        return (Collection<EventSubscription>) persistence.getSubcriptions(eventName, eventKey, effectiveDate);
    }
    
    protected void teardown() {
        host.stop();
    }   
}
