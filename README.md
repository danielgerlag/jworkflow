# JWorkflow

JWorkflow is a light weight workflow library for Java.  It supports pluggable persistence and concurrency providers to allow for multi-node clusters.

## Installing

### Using Maven

Add `jworkflow` to your POM file as a dependency.

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow</artifactId>
        <version>0.4</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow:0.4'
}
```

## Documentation

See [Tutorial here.](https://github.com/danielgerlag/jworkflow/tree/master/docs)

## Fluent API

Define your workflows with the fluent API.

```c#
public class HelloWorkflow implements Workflow {
   @Override
    public void build(WorkflowBuilder builder) {
        
        builder
            .startsWith(Task1.class)
            .then(Task2.class)                
            .then(Task3.class);        
    }    
}
...
...
public class Task1 implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Hello world");
        return ExecutionResult.next();
    }    
}
```

## JSON Workflow Definitions

Define your workflows in JSON

```json
{
  "id": "HelloWorld",
  "version": 1,
  "steps": [
    {
      "id": "hello",
      "stepType": "com.myapp.Step1",
      "nextStepId": "bye"
    },        
    {
      "id": "bye",
      "stepType": "com.myapp.Step2"
    }
  ]
}
```

### Sample use cases

* New user workflow
```java
public class MyData {    
    public String email;
    public String password;
    public String userId;
}


public class MyWorkflow implements Workflow<MyData> {

    ...

    @Override
    public void build(WorkflowBuilder<MyData> builder) {
        builder
            .startsWith(CreateUser.class)  
                .input((step, data) -> step.email = data.email)
                .input((step, data) -> step.password = data.password)
                .output((step, data) -> data.userId = step.userId)
            .then(SendConfirmationEmail.class)
                .waitFor("confirmation", data -> data.userId)
            .then(UpdateUser.class)
                .input((step, data) -> step.userId = data.userId);
    }    
}
```

* Saga Transactions

```java
public class MyWorkflow implements Workflow<MyData> {

    ...

    @Override
    public void build(WorkflowBuilder<MyData> builder) {
        builder
            .startsWith(CreateCustomer.class)  
            .then(PushToSalesforce.class)
                .onError(ErrorBehavior.RETRY)
            .then(PushToERP.class)
                .onError(ErrorBehavior.RETRY, Duration.ofMinutes(30));
    }    
}
```

```java
builder
    .startsWith(LogStart.class)  
    .saga(saga -> saga
        .startsWith(Task1.class)
            .compensateWith(UndoTask1.class)
        .then(Task2.Class)
            .compensateWith(UndoTask2.class)
        .then(Task3.Class)
            .compensateWith(UndoTask3.class)
    )
    .onError(ErrorBehavior.RETRY, Duration.ofMinutes(30));
    .then(LogEnd.class);
```


### Persistence

Since workflows are typically long running processes, they will need to be persisted to storage between steps.
There are several persistence providers available as seperate packages.

* Memory Persistence Provider *(Default provider, for demo and testing purposes)*
* [MongoDB](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.mongodb)
* *(more to come soon...)*

### Multi-node clusters

By default, the WorkflowHost service will run as a single node using the built-in queue and locking providers for a single node configuration.  Should you wish to run a multi-node cluster, you will need to configure an external queueing mechanism and a distributed lock manager to co-ordinate the cluster.  These are the providers that are currently available.

#### Queue Providers

* SingleNodeQueueProvider *(Default built-in provider)*
* [RabbitMQ](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.rabbitmq)
* [Redis](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.redis)
* [AWS Simple Queue Service](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.aws)

#### Distributed lock managers

* SingleNodeLockProvider *(Default built-in provider)*
* [Redis](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.redis)
* [AWS DynamoDB](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.aws)
* *(more to come soon...)*

## Samples

[Hello World](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample01)

[Passing Data](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample02)

[If condition](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample06)

[Responding to external events](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample03)

[Parallel ForEach](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample04)

[While loop](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample05)

[Saga Transactions](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample07)

[JSON Workflows](https://github.com/danielgerlag/jworkflow/tree/master/samples/sample08)



## Contributors

* **Daniel Gerlag** - *Initial work*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

