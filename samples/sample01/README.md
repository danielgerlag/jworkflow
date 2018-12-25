# Hello World Sample

Illustrates the basic usage of the fluent API for defining workflows.

```java
public class HelloWorkflow implements Workflow {

    @Override
    public void build(WorkflowBuilder builder) {
        builder
            .startsWith(Hello.class)                
            .then(Goodbye.class);
    }
    ...
}
```

and how to define steps

```java
public class Hello implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Hello world");
        return ExecutionResult.next();
    }    
}
```


