package com.jworkflow.kernel.scenarios;

import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.interfaces.Workflow;
import com.jworkflow.kernel.models.ExecutionResult;
import com.jworkflow.kernel.models.StepExecutionContext;
import com.jworkflow.kernel.models.WorkflowInstance;
import com.jworkflow.kernel.models.WorkflowStatus;
import com.jworkflow.kernel.scenarios.Scenario;
import com.jworkflow.kernel.services.TypedWorkflowBuilder;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BasicWorkflowScenario extends Scenario{
    
    private static int step1Ticker = 0;
    private static int step2Ticker = 0;
    
    static class Step1 implements StepBody {
        @Override
        public ExecutionResult run(StepExecutionContext context) {
            step1Ticker++;
            return ExecutionResult.next();
        }
    }
    
    static class Step2 implements StepBody {
        @Override
        public ExecutionResult run(StepExecutionContext context) {
            step2Ticker++;
            return ExecutionResult.next();
        }
    }
    
    class BasicWorkflow implements Workflow {

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
        public void build(TypedWorkflowBuilder builder) {
            builder.StartsWith(Step1.class)
                    .then(Step2.class);

        }
    }

    @Test
    public void test() throws Exception {
        WorkflowInstance result = runWorkflow(new BasicWorkflow(), null);
        
        assertEquals(WorkflowStatus.COMPLETE, result.getStatus());
        assertEquals(1, step1Ticker);
        assertEquals(1, step2Ticker);        
    }
    
}
