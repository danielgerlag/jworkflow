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
        <version>0.1</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow:0.1'
}
```


## Basic Concepts

### Steps

A workflow consists of a series of connected steps.  Each step produces an outcome value and subsequent steps are triggered by subscribing to a particular outcome of a preceeding step.  
The default outcome of `ExecutionResult.next()` can be used for a basic linear workflow.
Steps are usually defined by implementing from the `StepBody` interface.  They can also be created inline while defining the workflow structure.

First we define some steps

```java
public class Hello implements StepBody {

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        System.out.println("Hello world");
        return ExecutionResult.next();
    }    
}
```

Then we define the workflow structure by composing a chain of steps.  The is done by implementing `Workflow` interface

```java
public class HelloWorkflow implements Workflow {

    @Override
    public String getId() {
        return "hello";
    }

    @Override
    public Class getDataType() {
        return MyData.class;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void build(TypedWorkflowBuilder builder) {
        
        builder.startsWith(Hello.class)
                .then(Hello.class)                
                .then(Goodbye.class);        
    }    
}
```
The  id and version properties are used by the workflow host to identify a workflow definition.

You can also define your steps inline

```java
@Override
public void build(TypedWorkflowBuilder builder) {    
    builder.startsWith(Hello.class)
            .then(context -> { 
                return ExecutionResult.next(); 
            })                
            .then(Goodbye.class);    
}
```
*The `dataType` property on the `Workflow` interface is used to specify a strongly typed data class that will be persisted along with each instance of this workflow*

Each running workflow is persisted to the chosen persistence provider between each step, where it can be picked up at a later point in time to continue execution.  The outcome result of your step can instruct the workflow host to defer further execution of the workflow until a future point in time or in response to an external event.

The first time a particular step within the workflow is called, the persistenceData property on the context object is *null*.  The ExecutionResult produced by the *run* method can either cause the workflow to proceed to the next step by providing an outcome value, instruct the workflow to sleep for a defined period or simply not move the workflow forward.  If no outcome value is produced, then the step becomes re-entrant by setting persistenceData, so the workflow host will call this step again in the future buy will popluate the persistenceData with it's previous value.

For example, this step will initially run with *null* persistenceData and put the workflow to sleep for 1 hour, while setting the persistenceData to *true*.  1 hour later, the step will be called again but context.persistenceData will now contain the value from the previous iteration, and will now produce an outcome value of *null*, causing the workflow to move forward.

```java
class DeferredStep extends StepBody {    
    public run(context: StepExecutionContext): Promise<ExecutionResult> {
        if (context.getPersistenceData() == null) {            
            System.out.println("going to sleep...");                
            return ExecutionResult.sleep(Duration.ofHours(1), true);
        }
        else {            
            System.out.println("waking up...");
            return ExecutionResult.next();
        } 
    }
}
```

### Passing data between steps

Each step is intended to be a black-box, therefore they support inputs and outputs.  These inputs and outputs can be mapped to a data class that defines the custom data relevant to each workflow instance.

The following sample shows how to define inputs and outputs on a step, it then shows how define a workflow with a typed class for internal data and how to map the inputs and outputs to properties on the custom data class.

```java
//Our workflow step with inputs and outputs
public class AddNumbers implements StepBody {

    public int number1;
    public int number2;
    public int answer;
    
    @Override
    public ExecutionResult run(StepExecutionContext context) {
        answer = number1 + number2;
        return ExecutionResult.next();
    }    
}

//Our class to define the internal data of our workflow
public class MyData {    
    public int value1;
    public int value2;
    public int value3;
}

//Our workflow definition with strongly typed internal data and mapped inputs & outputs
public class DataWorkflow implements Workflow<MyData> {

    @Override
    public String getId() {
        return "data-workflow";
    }

    @Override
    public Class getDataType() {
        return MyData.class;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void build(TypedWorkflowBuilder<MyData> builder) {
        
        builder
            .startsWith(AddNumbers.class)  
                .input((step, data) -> step.number1 = data.value1)
                .input((step, data) -> step.number2 = data.value2)
                .output((step, data) -> data.value3 = step.answer)
            .then(DisplayAnswer.class)
                .input((step, data) -> step.answer = data.value3);
    }    
}
```

### Events

A workflow can also wait for an external event before proceeding.  In the following example, the workflow will wait for an event called *"myEvent"* with a key of *0*.  Once an external source has fired this event, the workflow will wake up and continue processing, passing the data generated by the event onto the next step.

```java
public void build(TypedWorkflowBuilder<MyData> builder) {    
    builder
        .startsWith(Hello.class)
        .waitFor("myEvent", x -> "0")
            .output((step, data) -> data.value1 = step.eventData)
        .then(DisplayAnswer.class)
            .input((step, data) -> step.answer = data.value1);
}
...
//External events are published via the host
//All workflows that have subscribed to myEvent 0, will be passed "hello"
host.publishEvent("myEvent", "0", "hello");
```

### Host

The workflow host is the service responsible for executing workflows.  It does this by polling the persistence provider for workflow instances that are ready to run, executes them and then passes them back to the persistence provider to by stored for the next time they are run.  It is also responsible for publishing events to any workflows that may be waiting on one.

#### Usage

When your application starts, create a WorkflowHost service, call *registerWorkflow*, so that the workflow host knows about all your workflows, and then call *start()* to fire up the thread pool that executes workflows.  Use the *startWorkflow* method to initiate a new instance of a particular workflow.


```java
WorkflowModule.setup();
WorkflowHost host = WorkflowModule.getHost();
        
host.registerWorkflow(HelloWorkflow.class);

host.start();

String id = host.startWorkflow("hello", 1, null);
```


### Persistence

Since workflows are typically long running processes, they will need to be persisted to storage between steps.
There are several persistence providers available as seperate packages.

* Memory Persistence Provider *(Default provider, for demo and testing purposes)*
* [MongoDB](jworkflow.providers.mongodb)
* *(more to come soon...)*

### Multi-node clusters

By default, the WorkflowHost service will run as a single node using the built-in queue and locking providers for a single node configuration.  Should you wish to run a multi-node cluster, you will need to configure an external queueing mechanism and a distributed lock manager to co-ordinate the cluster.  These are the providers that are currently available.

#### Queue Providers

* SingleNodeQueueProvider *(Default built-in provider)*
* RabbitMQ *(coming soon...)*
* Apache ZooKeeper *(coming soon...)*

#### Distributed lock managers

* SingleNodeLockProvider *(Default built-in provider)*
* Redis Redlock *(coming soon...)*
* Apache ZooKeeper *(coming soon...)*


## Samples

[Hello World](sample01)

[Passing Data](sample01)

[Responding to external events](sample01)


## Authors

* **Daniel Gerlag** - *Initial work*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details