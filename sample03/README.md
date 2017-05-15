# Events sample

Illustrates how to signal a workflow to wait for an external event, and how to invoke that event.

We use the .waitFor() method on the workflow builder to signal the current execution path to wait until an event with a particular name (myEvent) and key (1) is published to the workflow host.

```java
public class EventsWorkflow implements Workflow<MyData> {

    @Override
    public void build(WorkflowBuilder<MyData> builder) {        
        builder
            .startsWith(Hello.class)
            .waitFor("myEvent", x -> "1")
                .output((step, data) -> data.value1 = step.eventData)
            .then(DisplayAnswer.class)
                .input((step, data) -> step.answer = data.value1);
    }
    ...    
}
```
An event is published to all subscribed workflows via the Workflow Host service, where a data object can be passed to all workflows waiting for event (myEvent) with key 1 as at the current date.

```java
Scanner scanner = new Scanner(System.in);
System.out.println("Enter value to publish");
String inputData = scanner.nextLine();

host.publishEvent("myEvent", "1", inputData, new Date());
```

