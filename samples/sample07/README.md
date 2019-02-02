# Saga Transaction sample

Illustrates how to implement an saga transaction with compensating steps, should an unhandled exception occur within the saga.


```java
@Override
public void build(WorkflowBuilder<Object> builder) {        
    builder
        .startsWithAction(context -> System.out.println("begin"))
        .saga(saga -> saga
            .startsWith(Task1.class)
                .compensateWith(UndoTask1.class)
            .then(Task2.class)
                .compensateWith(UndoTask2.class)
            .then(Task3.class)
                .compensateWith(UndoTask3.class)
        )
        .thenAction(context -> System.out.println("end")); 
}    
```
