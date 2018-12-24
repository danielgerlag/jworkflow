# AWS providers for JWorkflow

Provides support to run multi-node clusters of [JWorkflow](../README.md), by providing a distributed shared work queue.

## Installing

### Using Maven

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow.providers.aws</artifactId>
        <version>0.4</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow.providers.aws:0.4'
}
```


## Usage

```java
import software.amazon.awssdk.regions.Region;
import net.jworkflow.providers.aws.SQSProvider;
...
...

```java
WorkflowModule module = new WorkflowModule();

module.useQueue(new SQSProvider(Region.US_WEST_1));

module.build();
WorkflowHost host = module.getHost();

```
