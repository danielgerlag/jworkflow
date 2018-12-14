package net.jworkflow.kernel.scenarios;

import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.interfaces.Workflow;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.models.StepExecutionContext;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.WorkflowStatus;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;

public class WhileScenario extends Scenario {
    
    private static int step1Ticker = 0;
    private static int step2Ticker = 0;
    private static int step3Ticker = 0;
    
    public class MyData {
        public int counter;
    }
    
    static class DoSomething implements StepBody {

        public int increment;
        
        @Override
        public ExecutionResult run(StepExecutionContext context) {
            step2Ticker++;
            increment = 1;
            return ExecutionResult.next();
        }    
    }
        
    class ScenarioWorkflow implements Workflow<MyData> {

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
        public void build(WorkflowBuilder<MyData> builder) {
            builder
                .startsWith(context -> {
                    step1Ticker++;
                    return ExecutionResult.next();
                })
                .while(data -> data.counter < 3)
                    .Do(each -> each
                        .startsWith(DoSomething.class)
                            .output((step, data) -> data.counter += step.increment)
                    )
                .then(context -> {
                    step3Ticker++;
                    return ExecutionResult.next();
                });
        }
    }

    @Test
    public void test() throws Exception {
        MyData data = new MyData();
        data.counter = 0;
        WorkflowInstance result = runWorkflow(new ScenarioWorkflow(), data);
        
        assertEquals(WorkflowStatus.COMPLETE, result.getStatus());
        assertEquals(1, step1Ticker);
        assertEquals(3, step2Ticker);
        assertEquals(1, step3Ticker);        
        assertEquals(3, data.counter);
    }    
}
