package net.jworkflow.kernel.scenarios;

import net.jworkflow.kernel.interfaces.Workflow;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;
import net.jworkflow.kernel.services.abstractions.WorkflowTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventScenario extends WorkflowTest {

    private static int step1Ticker = 0;
    private static int step2Ticker = 0;
    
    class MyData {    
        public int value1;
    }
    
    class EventWorkflow extends WorkflowTest.TestableWorkflow<MyData> {

        @Override
        public void build(WorkflowBuilder<MyData> builder) {
            builder
                .startsWithAction(context -> step1Ticker++)
                .waitFor("event", data -> "key")
                    .output((step, data) -> data.value1 = (int)step.eventData)
                .thenAction(context -> step2Ticker++);
        }
    }    
    
    @Override
    protected Workflow getWorkflow() {
        return new EventWorkflow();
    }
    
    @Test
    public void Scenario() throws Exception {
        setup();
        MyData data = new MyData();
        String workflowId = startWorkflow(data);
        
        waitForEventSubscription("event", "key");
        assertEquals(1, step1Ticker);
        assertEquals(0, step2Ticker);        
        host.publishEvent("event", "key", 7, null);
        waitForWorkflowToComplete(workflowId);
        
        assertEquals(1, step1Ticker);
        assertEquals(1, step2Ticker);
        assertEquals(7, data.value1);
    }
    
}
