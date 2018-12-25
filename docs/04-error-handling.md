### Error handling

Each step can be configured with it's own error handling behavior, it can be retried at a later time, suspend the workflow or terminate the workflow.

```java
public void build(WorkflowBuilder<object> builder) {    
    builder
        .startsWith(Hello.class)
        .then(DoSomething.class)
            .onError(ErrorBehavior.RETRY, Duration.ofMinutes(30));
}
```
