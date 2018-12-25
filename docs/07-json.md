## Loading workflow definitions from JSON

Simply call `registerWorkflowFromJson` on the `WorkflowHost` instance.

```java
WorkflowModule module = new WorkflowModule();
module.build();
WorkflowHost host = module.getHost();
host.registerWorkflowFromJson(str);
```

## Format of the JSON definition

### Basics

The JSON format defines the steps within the workflow by referencing the fully qualified class names.

| Field                   | Description                 |
| ----------------------- | --------------------------- |
| id                      | Workflow Definition ID        |
| version                 | Workflow Definition Version   |
| dataType                | Fully qualified class name of the custom data object            |
| steps[].id              | Step ID (required unique key for each step)                     |
| steps[].stepType        | Fully qualified class name of the step                          |
| steps[].nextStepId      | Step ID of the next step after this one completes               |
| steps[].inputs          | Optional Key/value pair of step inputs                          |
| steps[].outputs         | Optional Key/value pair of step outputs                         |
| steps[].cancelCondition | Optional cancel condition                                       |

```json
{
  "id": "hello-workflow",
  "version": 1,
  "steps": [
    {
      "id": "step1",
      "stepType": "net.jworkflow.sample08.steps.Hello",
      "nextStepId": "step2"
    },
    {
      "id": "step2",
      "stepType": "net.jworkflow.sample08.steps.Goodbye"
    }
  ]
}
```

### Inputs and Outputs

Inputs and outputs can be bound to a step as a key/value pair object, 
* The `inputs` collection, the key would match a property on the `Step` class and the value would be an expression with both the `data` and `context` parameters at your disposal.
* The `outputs` collection, the key would match a property on the `Data` class and the value would be an expression with both the `step` as a parameter at your disposal.

The underlying expression language is Javascript.

```json
{
  "id": "test-workflow",
  "version": 1,
  "dataType": "net.jworkflow.sample08.MyData",
  "steps": [
    {
      "id": "step1",
      "stepType": "net.jworkflow.sample08.steps.Hello",
      "nextStepId": "step2"
    },
    {
      "id": "step2",
      "stepType": "net.jworkflow.sample08.steps.AddNumbers",
      "nextStepId": "step3",
      "inputs": {
          "value1": "data.value1",
          "value2": "data.value2"
      },
      "outputs": {
          "value3": "step.result"
      }
    },
    {
      "id": "step3",
      "stepType": "net.jworkflow.sample08.steps.PrintMessage",
      "nextStepId": "step4",
      "inputs": {
          "message": "'The answer is ' + data.value3"
      }
    },
    {
      "id": "step4",
      "stepType": "net.jworkflow.sample08.steps.Goodbye"
    }
  ]
}
```

### WaitFor

The `.waitFor` can be implemented using 3 inputs as follows

| Field                  | Description                 |
| ---------------------- | --------------------------- |
| cancelCondition        | Optional expression to specify a cancel condition  |
| inputs.eventName       | Expression to specify the event name               |
| inputs.eventKey        | Expression to specify the event key                |
| inputs.effectiveDate   | Optional expression to specify the effective date  |


```json
{
    "id": "MyWaitStep",
    "stepType": "net.jworkflow.primitives.WaitFor",
    "nextStepId": "...",
    "cancelCondition": "...",
    "inputs": {
        "eventName": "\"Event1\"",
        "eventKey": "\"Key1\"",
        "effectiveDate": "new Date()"
    }
}
```

### If

The `.If` can be implemented as follows

```json
{
  "id": "test-workflow",
  "version": 1,
  "dataType": "net.jworkflow.sample08.MyData",
  "steps": [
    {
      "id": "step1",
      "stepType": "net.jworkflow.sample08.steps.Hello",
      "nextStepId": "step2"
    },
    {
      "id": "step2",
      "stepType": "net.jworkflow.primitives.If",
      "nextStepId": "step3",
      "inputs": {
          "condition": "data.value1 == 2"
      },
      "thenDo": [[
          {
            "id": "step2.1",
            "stepType": "net.jworkflow.sample08.steps.PrintMessage",
            "nextStepId": "step2.2",
            "inputs": {
                "message": "'doing 2.1'"
            }
          },
          {
            "id": "step2.2",
            "stepType": "net.jworkflow.sample08.steps.PrintMessage",
            "inputs": {
                "message": "'doing 2.2'"
            }
          }
      ]]
    },    
    {
      "id": "step3",
      "stepType": "net.jworkflow.sample08.steps.Goodbye"
    }
  ]
}
```

### While

The `.While` can be implemented as follows

```json
{
  "id": "test-workflow",
  "version": 1,
  "dataType": "net.jworkflow.sample08.MyData",
  "steps": [
    {
      "id": "step1",
      "stepType": "net.jworkflow.sample08.steps.Hello",
      "nextStepId": "step2"
    },
    {
      "id": "step2",
      "stepType": "net.jworkflow.primitives.While",
      "nextStepId": "step3",
      "inputs": {
          "condition": "data.value == 1"
      },
      "thenDo": [[
          {
            "id": "step2.1",
            "stepType": "net.jworkflow.sample08.steps.PrintMessage",
            "inputs": {
                "message": "'doing 2.1'"
            }
          }
      ]]
    },    
    {
      "id": "step3",
      "stepType": "net.jworkflow.sample08.steps.Goodbye"
    }
  ]
}
```

### ForEach

The `.forEach` can be implemented as follows

```json
{
  "id": "test-workflow",
  "version": 1,
  "dataType": "net.jworkflow.sample08.MyData",
  "steps": [
    {
      "id": "step1",
      "stepType": "net.jworkflow.sample08.steps.Hello",
      "nextStepId": "step2"
    },
    {
      "id": "step2",
      "stepType": "net.jworkflow.primitives.Foreach",
      "nextStepId": "step3",
      "inputs": {
          "collection": "data.collection1"
      },
      "thenDo": [[
          {
            "id": "step2.1",
            "stepType": "net.jworkflow.sample08.steps.PrintMessage",
            "inputs": {
                "message": "'doing 2.1'"
            }
          }
      ]]
    },    
    {
      "id": "step3",
      "stepType": "net.jworkflow.sample08.steps.Goodbye"
    }
  ]
}
```

### Delay

The `.delay` can be implemented as follows

```json
{
      "id": "MyDelayStep",
      "stepType": "net.jworkflow.primitives.Delay",
      "nextStepId": "...",
      "inputs": { "duration": "<<expression to evaluate>>" }
}
```


### Parallel

The `.parallel` can be implemented as follows

```json
{
      "id": "MyParallelStep",
      "stepType": "net.jworkflow.primitives.Sequence",
      "nextStepId": "...",
      "thenDo": [
		[ /* Branch 1 */
		  {
		    "id": "Branch1.Step1",
		    "stepType": "...",
		    "nextStepId": "Branch1.Step2"
		  },
		  {
		    "id": "Branch1.Step2",
		    "stepType": "..."
		  }
		],			
		[ /* Branch 2 */
		  {
		    "id": "Branch2.Step1",
		    "stepType": "...",
		    "nextStepId": "Branch2.Step2"
		  },
		  {
		    "id": "Branch2.Step2",
		    "stepType": "..."
		  }
		]
	  ]
}
```

### Schedule

The `.schedule` can be implemented as follows

```json
{
      "id": "MyScheduleStep",
      "stepType": "net.jworkflow.primitives.Schedule",
      "inputs": { "duration": "<<expression to evaluate>>" },
      "thenDo": [[
          {
            "id": "do1",
            "stepType": "...",
            "nextStepId": "do2"
          },
          {
            "id": "do2",
            "stepType": "..."
          }
      ]]
}
```

### Recur

The `.recur` can be implemented as follows

```json
{
      "id": "MyScheduleStep",
      "stepType": "net.jworkflow.primitives.Recur",
      "inputs": { 
        "interval": "<<expression to evaluate>>",
        "stopCondition": "<<expression to evaluate>>" 
      },
      "thenDo": [[
          {
            "id": "do1",
            "stepType": "...",
            "nextStepId": "do2"
          },
          {
            "id": "do2",
            "stepType": "..."
          }
      ]]
}
```
