# MongoDB Persistence provider for JWorkflow

Provides support to persist workflows running on [JWorkflow](../README.md) to a MongoDB database.

## Installing

### Using Maven

```xml
<dependencies>
    <dependency>
        <groupId>net.jworkflow</groupId>
        <artifactId>jworkflow.providers.mongodb</artifactId>
        <version>0.2</version>
    </dependency>
</dependencies>
```

### Using Gradle

```Gradle
dependencies { 
    compile 'net.jworkflow:jworkflow.providers.mongodb:0.2'
}
```


## Usage

Use the MongoPersistenceService.configure static method when bootstraping your application.

```java
import net.jworkflow.providers.mongodb.MongoPersistenceService;

...

WorkflowModule.setup(MongoPersistenceService.configure("mongodb://localhost:27017/jworkflow"));
```
