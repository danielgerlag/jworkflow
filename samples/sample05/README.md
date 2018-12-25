# While sample

Illustrates how to implement a while loop within your workflow.


```java
public class WhileWorkflow implements Workflow<MyData> {

    @Override
    public void build(WorkflowBuilder<MyData> builder) {
        builder
            .startsWith(Hello.class)                
            .While(data -> data.value1 < 3)
                .Do(each -> each
                    .startsWith(IncrementValue.class)
                        .input((step, data) -> step.value = data.value1)
                        .output((step, data) -> data.value1 = step.value)
                )
            .then(Goodbye.class);        
    }    
   ...
}
```
