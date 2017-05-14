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

public class DataIOScenario extends Scenario{
    
    private static int step1Ticker = 0;
    private static int step2Ticker = 0;
    private static int finalResult = 0;
    
    public class MyData {    
        public int value1;
        public int value2;
        public int value3;
    }
    
    static class AddNumbers implements StepBody {

        public int number1;
        public int number2;
        public int answer;

        @Override
        public ExecutionResult run(StepExecutionContext context) {
            step1Ticker++;
            answer = number1 + number2;
            return ExecutionResult.next();
        }    
    }
    
    static class GetResult implements StepBody {
        
        public int result;
        
        @Override
        public ExecutionResult run(StepExecutionContext context) {
            step2Ticker++;
            finalResult = result;
            return ExecutionResult.next();
        }
    }
    
    class BasicWorkflow implements Workflow<MyData> {

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
                .startsWith(AddNumbers.class)
                    .input((step, data) -> step.number1 = data.value1)
                    .input((step, data) -> step.number2 = data.value2)
                    .output((step, data) -> data.value3 = step.answer)
                .then(GetResult.class)
                    .input((step, data) -> step.result = data.value3);
        }
    }

    @Test
    public void test() throws Exception {
        MyData data = new MyData();
        data.value1 = 2;
        data.value2 = 3;
        WorkflowInstance result = runWorkflow(new BasicWorkflow(), data);
        
        assertEquals(WorkflowStatus.COMPLETE, result.getStatus());
        assertEquals(1, step1Ticker);
        assertEquals(1, step2Ticker);        
        assertEquals(5, finalResult);
    }    
}
