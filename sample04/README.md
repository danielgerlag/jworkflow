# Foreach sample

Illustrates how to implement a parallel foreach within your workflow.


```java
public class EventsWorkflow implements Workflow<MyData> {

    @Override
    public void build(WorkflowBuilder<MyData> builder) {        
        
        builder
            .startsWith(Hello.class)                          
            .foreach(data -> new String[] { "item 1", "item 2", "item 3" })
                .Do(each -> each
                    .startsWith(DoSomething.class))
            .then(Goodbye.class);        
    }
    ...    
}
```

or get the collectioin from workflow data.

```java
public class EventsWorkflow implements Workflow<MyData> {

    @Override
    public void build(WorkflowBuilder<MyData> builder) {        
        
        builder
            .startsWith(Hello.class)                          
            .foreach(data -> data.value1)
                .Do(each -> each
                    .startsWith(DoSomething.class))
            .then(Goodbye.class);        
    }
    ...    
}
```

Access the iteration item from the step execution context

```java
public class DoSomething implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Doing something with " + context.getItem());
        return ExecutionResult.next();
    }    
}
```
