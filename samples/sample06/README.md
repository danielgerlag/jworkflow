# If sample

Illustrates how to implement an If decision within your workflow.


```java
public class IfWorkflow implements Workflow<MyData> {

    @Override
    public void build(WorkflowBuilder<MyData> builder) {        
        builder
            .startsWith(Hello.class)                          
            .If(data -> data.value1 > 2)
                .Do(then -> then
                    .startsWith(PrintMessage.class)
                        .input((step, data) -> step.message = "Value is greater than 2")
                    .then(PrintMessage.class)
                        .input((step, data) -> step.message = "Doing something...")
                )
            .If(data -> data.value1 == 5)
                .Do(then -> then
                    .startsWith(PrintMessage.class)
                        .input((step, data) -> step.message = "Value is 5")
                    .then(PrintMessage.class)
                        .input((step, data) -> step.message = "Doing something...")
                )
            .then(Goodbye.class);        
    }
	...    
}
```
