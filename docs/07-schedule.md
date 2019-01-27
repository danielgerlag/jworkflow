### Schedule

Use `.schedule` to register a future set of steps to run asynchronously in the background within your workflow.


```java
@Override
public void build(WorkflowBuilder<MyData> builder) {
    builder
        .startsWith(Hello.class)                          
        .schedule(data -> Duration.ofMinutes(10)) 
            .Do(schedule -> schedule
                .startsWith(DoSomething.class)
                .then(DoSomethingElse.class)
            )
        .then(Goodbye.class);        
}    
```
