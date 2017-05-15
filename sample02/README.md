# Passing data between steps sample

Illustrates how to define a data class for your workflow and wire it's properties up to the inputs and outputs of steps.

First, we define a class to hold data for our workflow.

```java
public class MyData {    
    public int value1;
    public int value2;
    public int value3;
}
```

Then we create a step with inputs and outputs, by simply exposing public properties.

```java
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
```

Then we put it all together in a workflow.

```java
public class DataWorkflow implements Workflow<MyData> {

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

    ...    
}
```

