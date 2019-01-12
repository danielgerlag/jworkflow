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

Then we define the workflow structure by composing a chain of steps.  The is done by implementing the `Workflow` interface

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
    public void build(WorkflowBuilder builder) {
        
        builder
            .startsWith(Hello.class)
            .then(Hello.class)                
            .then(Goodbye.class);        
    }    
}
```
The  id and version properties are used by the workflow host to identify a workflow definition.

You can also define your steps inline

```java
@Override
public void build(WorkflowBuilder builder) {    
    builder
        .startsWith(Hello.class)
        .thenAction(context -> System.out.println("doing something..."))
        .then(Goodbye.class);    
}
```
*The `dataType` property on the `Workflow` interface is used to specify a strongly typed data class that will be persisted along with each instance of this workflow*

Each running workflow is persisted to the chosen persistence provider between each step, where it can be picked up at a later point in time to continue execution.  The outcome result of your step can instruct the workflow host to defer further execution of the workflow until a future point in time or in response to an external event.

The first time a particular step within the workflow is called, the persistenceData property on the context object is *null*.  The ExecutionResult produced by the *run* method can either cause the workflow to proceed to the next step by providing an outcome value, instruct the workflow to sleep for a defined period or simply not move the workflow forward.  If no outcome value is produced, then the step becomes re-entrant by setting persistenceData, so the workflow host will call this step again in the future but will popluate the persistenceData with it's previous value.

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



### Host

The workflow host is the service responsible for executing workflows.  It does this by polling the persistence provider for workflow instances that are ready to run, executes them and then passes them back to the persistence provider to be stored for the next time they are run.  It is also responsible for publishing events to any workflows that may be waiting on one.

#### Usage

When your application starts, create a WorkflowHost service, call *registerWorkflow*, so that the workflow host knows about all your workflows, and then call *start()* to fire up the thread pool that executes workflows.  Use the *startWorkflow* method to initiate a new instance of a particular workflow.


```java
WorkflowModule module = new WorkflowModule();
module.build();
WorkflowHost host = module.getHost();
        
host.registerWorkflow(HelloWorkflow.class);

host.start();

String id = host.startWorkflow("hello", 1, null);
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
* [AWS Simple Queue Service](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.aws)
* [Redis](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.redis)

#### Distributed lock managers

* Single Lock Manager *(Default built-in provider)*
* [Redis](https://github.com/danielgerlag/jworkflow/tree/master/jworkflow.providers.redis)



## Injecting dependencies into steps

Illustrates the use of dependency injection for workflow steps.

Consider the following service

```java
public interface MyService {
    void doTheThings();
}
...
public class DefaultMyService implments MyService{
    public void doTheThings() {
        System.out.println("Doing stuff...");
    }
}
```

Which is consumed by a workflow step as follows

```java
public class DoSomething implements StepBody {
    
    private final MyService myService;

    @Inject
    public DoSomething(MyService myService) {
        myService = myService;
    }

    @Override
    public ExecutionResult run(StepExecutionContext context) {
        myService.doTheThings();
        return ExecutionResult.next();
    }
}
```

Simply add configure the service with Google Guice

```java
bind(MyService.class).to(DefaultMyService.class);
```


