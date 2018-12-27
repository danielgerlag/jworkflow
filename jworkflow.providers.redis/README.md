# Redis providers for JWorkflow

Provides support to run multi-node clusters of [JWorkflow](../README.md), by providing a distributed lock manager and/or a shared work queue.

## Installing

### Using Maven

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow.providers.redis</artifactId>
        <version>>0.5-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow.providers.redis:>0.5-SNAPSHOT'
}
```


## Usage

```java
import org.redisson.config.Config;
import net.jworkflow.providers.redis.RedisLockServiceProvider;
...

and / or
```java
import net.jworkflow.providers.redis.RedisQueueServiceProvider;
...
...

```java
WorkflowModule module = new WorkflowModule();

Config config = new Config();
config.useSingleServer().setAddress("redis://127.0.0.1:6379");

module.useDistibutedLock(new RedisLockServiceProvider(config));
module.useQueue(new RedisQueueServiceProvider(config));

module.build();
WorkflowHost host = module.getHost();

```
