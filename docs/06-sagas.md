# Saga transaction with compensation

A Saga allows you to encapsulate a sequence of steps within a saga transaction and specify compensation steps for each.

In the sample, `Task2` will throw an exception, then `UndoTask2` and `UndoTask1` will be triggered.

```java
builder
    .startsWithAction(context -> System.out.println("begin"))
    .saga(saga -> saga
        .startsWith(Task1.class)
            .compensateWith(UndoTask1.class)
        .then(Task2.class)
            .compensateWith(UndoTask2.class)
        .then(Task3.class)
            .compensateWith(UndoTask3.class)
    )
    .onError(ErrorBehavior.RETRY, Duration.ofMinutes(1));
    .thenAction(context -> System.out.println("end")); 
```

## Retry policy for failed saga transaction

In the previous example, the saga will retry the saga every 1 minute, but you could also simply fail completely, and process a master compensation task for the whole saga.

```java
builder
    .startsWithAction(context -> System.out.println("begin"))
    .saga(saga -> saga
        .startsWith(Task1.class)
            .compensateWith(UndoTask1.class)
        .then(Task2.class)
            .compensateWith(UndoTask2.class)
        .then(Task3.class)
            .compensateWith(UndoTask3.class)
    )
    .compensateWith(CleanUp.class)
    .thenAction(context -> System.out.println("end")); 
```

## Compensate entire saga transaction

You could also only specify a master compensation step, as follows

```java
builder
    .startsWithAction(context -> System.out.println("begin"))
    .saga(saga -> saga
        .startsWith(Task1.class)
        .then(Task2.class)
        .then(Task3.class)
    )
    .compensateWith(UndoEverything.class)
    .thenAction(context -> System.out.println("end")); 
```

## Passing parameters to compensation steps

Parameters can be passed to a compensation step as follows

```java
builder
    .startsWith(SayHello.class)
        .compensateWith(PrintMessage.class, compensate -> {
            compensate.input(step, data -> step.Message = "undoing...");
        });
```

## Expressing a saga in JSON

A saga transaction can be expressed in JSON, by using the `net.jworkflow.primitives.Sequence` step and setting the `saga` parameter to `true`.

The compensation steps can be defined by specifying the `compensateWith` parameter.

```json
{
  "id": "test-workflow",
  "version": 1,
  "steps": [
    {
      "id": "hello",
      "stepType": "net.jworkflow.sample08.steps.Hello",
      "nextStepId": "mySaga"
    },
    {
      "id": "mySaga",
      "stepType": "net.jworkflow.primitives.Sequence",
      "nextStepId": "bye",
      "saga": true,
      "thenDo": [
        [
          {
            "id": "do1",
            "stepType": "net.jworkflow.sample08.steps.Task1",
            "nextStepId": "do2",
            "compensateWith": [
              {
                "id": "undo1",
                "stepType": "net.jworkflow.sample08.steps.UndoTask1"
              }
            ]
          },
          {
            "id": "do2",
            "stepType": "net.jworkflow.sample08.steps.Task2",
            "compensateWith": [
              {
                "id": "undo2",
                "stepType": "net.jworkflow.sample08.steps.UndoTask2"
              }
            ]
          },
          {
            "id": "do3",
            "stepType": "net.jworkflow.sample08.steps.Task3",
            "compensateWith": [
              {
                "id": "undo3",
                "stepType": "net.jworkflow.sample08.steps.UndoTask3"
              }
            ]
          }
        ]
      ]
    },    
    {
      "id": "bye",
      "stepType": "net.jworkflow.sample08.steps.Goodbye"
    }
  ]
}
```