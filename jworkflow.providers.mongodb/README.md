# MongoDB Persistence provider for JWorkflow

Provides support to persist workflows running on [JWorkflow](../README.md) to a MongoDB database.

## Usage

Use the MongoPersistenceService.configure static method when bootstraping your application.

```java
import com.jworkflow.providers.mongodb.MongoPersistenceService;

...

WorkflowModule.setup(MongoPersistenceService.configure("mongodb://localhost:27017/jworkflow"));
```
