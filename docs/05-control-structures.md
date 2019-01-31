## Control Structures

### If condition

```java
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
        .If(data -> data.value1 == 3)
            .Do(then -> then
                .startsWith(PrintMessage.class)
                    .input((step, data) -> step.message = "Value is 3")
                .then(PrintMessage.class)
                    .input((step, data) -> step.message = "Doing something...")
            )                
        .then(Goodbye.class);        
}    
```

### While loop

```java
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
```
### Parallel ForEach

```java
@Override
public void build(WorkflowBuilder<MyData> builder) {
    
    builder
        .startsWith(Hello.class)                          
        .foreach(data -> data.value1)  //either values from workflow data
            .Do(each -> each
                .startsWith(DoSomething.class))
        .then(Hello.class)
        .foreach(data -> new String[] { "item 1", "item 2", "item 3" })  //or values defined inline
            .Do(each -> each
                .startsWith(DoSomething.class))
        .then(Goodbye.class);        
}    
```

#### Parallel Paths

Use the .parallel() method to branch parallel tasks

```java
@Override
public void build(WorkflowBuilder<MyData> builder) {
    
    builder
        .startsWith(Hello.class)                          
        .parallel()
            .Do(p1 -> p1
                .startsWith(DoSomething.class)
                .then(DoSomethingElse.class);
            )
            .Do(p2 -> p2
                .startsWith(DoSomethingAmazing.class)
                .then(DoSomething.class);
            )
            .Do(p3 -> p3
                .startsWith(DoSomethingDodgy.class)
                .then(DoSomethingElse.class);
            )        
        .then(Goodbye.class);        
}    
```