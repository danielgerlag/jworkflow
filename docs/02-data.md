### Passing data between steps

Each step is intended to be a black-box, therefore they support inputs and outputs.  These inputs and outputs can be mapped to a data class that defines the custom data relevant to each workflow instance.

The following sample shows how to define inputs and outputs on a step, it then shows how define a workflow with a typed class for internal data and how to map the inputs and outputs to properties on the custom data class.

```java
//Our workflow step with inputs and outputs
public class AddNumbers implements StepBody {

    public int number1;
    public int number2;
    public int answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        answer = number1 + number2;
        return ExecutionResult.next();
    }    
}

//Our class to define the internal data of our workflow
public class MyData {    
    public int value1;
    public int value2;
    public int value3;
}

//Our workflow definition with strongly typed internal data and mapped inputs & outputs
public class DataWorkflow implements Workflow<MyData> {

    @Override
    public String getId() {
        return "data-workflow";
    }

    @Override
    public Class getDataType() {
        return MyData.class;
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
            .then(DisplayAnswer.class)
                .input((step, data) -> step.answer = data.value3);
    }    
}
```