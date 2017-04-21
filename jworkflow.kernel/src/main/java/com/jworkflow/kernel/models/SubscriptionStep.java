package com.jworkflow.kernel.models;

import com.google.inject.Injector;
import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.interfaces.StepBody;
import com.jworkflow.kernel.interfaces.WorkflowHost;
import java.util.Date;
import java.util.function.Function;

public class SubscriptionStep extends WorkflowStep {
    
    public Function<Object, String> eventKey;
    public String eventName;
    public Function<Object, Date> effectiveDate;

    @Override
    public StepBody constructBody(Injector injector) throws InstantiationException, IllegalAccessException {
        return new SubscriptionStepBody();
    }

    @Override
    public ExecutionPipelineResult initForExecution(WorkflowHost host, PersistenceProvider persistenceStore, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer)
    {
        if (!executionPointer.eventPublished)
        {
            if (eventKey != null)                
                executionPointer.eventKey = eventKey.apply(workflow.getData());

            Date date = new Date(0);

            if (effectiveDate != null)
                date = effectiveDate.apply(workflow.getData());

            executionPointer.eventName = eventName;
            executionPointer.active = false;
            persistenceStore.persistWorkflow(workflow);
            host.subscribeEvent(workflow.getId(), executionPointer.stepId, executionPointer.eventName, executionPointer.eventKey, date);

            return ExecutionPipelineResult.DEFER;
        }
        return ExecutionPipelineResult.NEXT;
    }

    @Override
    public ExecutionPipelineResult beforeExecute(WorkflowHost host, PersistenceProvider persistenceStore, StepExecutionContext context, ExecutionPointer executionPointer, StepBody body) {
        if (executionPointer.eventPublished)
        {
            if (body instanceof SubscriptionStepBody) {
                SubscriptionStepBody subBody = (SubscriptionStepBody)body;
                subBody.eventData = executionPointer.eventData;
            }                
        }
        return ExecutionPipelineResult.NEXT;
    }
}
