# RabbitMQ provider for JWorkflow

Provides support to run multi-node clusters of [JWorkflow](../README.md), by providing a distributed shared work queue.

## Installing

### Using Maven

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow.providers.rabbitmq</artifactId>
        <version>0.4</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow.providers.rabbitmq:0.4'
}
```


## Usage

```java
import com.rabbitmq.client.ConnectionFactory;
import net.jworkflow.providers.rabbitmq.RabbitMQProvider;
...
...

```java
WorkflowModule module = new WorkflowModule();

ConnectionFactory cf = new ConnectionFactory();
cf.setUsername("guest");
cf.setPassword("guest");
cf.setVirtualHost("/");
cf.setHost("localhost");
cf.setPort(5672);

module.useQueue(new RabbitMQProvider(cf));

module.build();
WorkflowHost host = module.getHost();

```
