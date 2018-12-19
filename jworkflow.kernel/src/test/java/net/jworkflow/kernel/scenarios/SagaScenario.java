package net.jworkflow.kernel.scenarios;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.interfaces.Workflow;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;
import net.jworkflow.kernel.services.abstractions.WorkflowTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class SagaScenario extends WorkflowTest {

    private static int beginTicker = 0;
    private static int endTicker = 0;
    
    private static int step1Ticker = 0;
    private static int step2Ticker = 0;
    private static int step3Ticker = 0;
    
    private static int undoStep1Ticker = 0;
    private static int undoStep2Ticker = 0;
    private static int undoStep3Ticker = 0;
    
    static class ExplodingStep implements StepBody {

        @Override
        public ExecutionResult run(StepExecutionContext context) throws Exception {
            step2Ticker++;
            throw new Exception();
        }    
    }
        
    class EventWorkflow implements Workflow<Object> {
        
        @Override
        public String getId() {
            return "scenario";
        }

        @Override
        public Class getDataType() {
            return Object.class;
        }

        @Override
        public int getVersion() {
            return 1;
        }

        @Override
        public void build(WorkflowBuilder<Object> builder) {
            builder
                .startsWithAction(context -> beginTicker++)
                .saga(saga -> saga
                .startsWithAction(context -> step1Ticker++)
                    .compensateWithAction(context -> undoStep1Ticker++)
                .then(ExplodingStep.class)
                    .compensateWithAction(context -> undoStep2Ticker++)
                .thenAction(context -> step3Ticker++)
                    .compensateWithAction(context -> undoStep3Ticker++)
            )
            .thenAction(context -> endTicker++);
        }
    }    
    
    @Override
    protected Workflow getWorkflow() {
        return new EventWorkflow();
    }
    
    @Test
    public void Scenario() throws Exception {
        setup();
        
        String workflowId = startWorkflow(null);
        waitForWorkflowToComplete(workflowId);
        
        assertEquals(1, beginTicker);
        assertEquals(1, step1Ticker);
        assertEquals(1, step2Ticker);
        assertEquals(0, step3Ticker);
        assertEquals(1, undoStep1Ticker);
        assertEquals(1, undoStep2Ticker);
        assertEquals(0, undoStep3Ticker);
        assertEquals(1, endTicker);        
    }    
}
