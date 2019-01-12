# AWS providers for JWorkflow

* Provides support to persist long running workflows for [JWorkflow](../README.md) backed by Amazon DynamoDB
* Provides support to run multi-node clusters of [JWorkflow](../README.md), by providing a distributed lock manager (via DynamoDB) and/or a shared work queue (via Simple Queue Service).

## Installing

### Using Maven

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow.providers.aws</artifactId>
        <version>>0.5-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow.providers.aws:>0.5-SNAPSHOT'
}
```

## Usage for Persistence


```java
import software.amazon.awssdk.regions.Region;
import net.jworkflow.providers.aws.DynamoDBPersistenceProvider;
...
...

```java
WorkflowModule module = new WorkflowModule();

module.usePersistence(new DynamoDBPersistenceProvider(Region.US_WEST_1, "table-prefix"));

module.build();
WorkflowHost host = module.getHost();

```


## Usage for Shared Work Queue

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


## Usage for Distributed Lock Manager

```java
import software.amazon.awssdk.regions.Region;
import net.jworkflow.providers.aws.DynamoDBLockProvider;
...
...

```java
WorkflowModule module = new WorkflowModule();

module.useDistibutedLock(new DynamoDBLockProvider(Region.US_WEST_1, "jworkflowLockTable")); //DynamoDB table name of your choice

module.build();
WorkflowHost host = module.getHost();

```
