# MongoDB Persistence provider for JWorkflow

Provides support to persist workflows running on [JWorkflow](../README.md) to a MongoDB database.

## Installing

### Using Maven

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow.providers.mongodb</artifactId>
        <version>0.4</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow.providers.mongodb:0.4'
}
```


## Usage

Use the MongoPersistenceService.configure static method when bootstraping your application.

```java
import net.jworkflow.providers.mongodb.MongoPersistenceService;

...

WorkflowModule module = new WorkflowModule();
module.usePersistence(MongoPersistenceService.configure("mongodb://localhost:27017/jworkflow"));
module.build();
WorkflowHost host = module.getHost();

```
