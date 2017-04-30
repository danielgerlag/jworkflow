package net.jworkflow.kernel.models;

import com.google.inject.Injector;
import net.jworkflow.kernel.interfaces.StepBody;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import java.util.Date;
import java.util.function.Function;
import net.jworkflow.kernel.interfaces.PersistenceService;

public class SubscriptionStep<TData> extends WorkflowStep {
    
    public Function<TData, String> eventKey;
    public String eventName;
    public Function<TData, Date> effectiveDate;

    @Override
    public StepBody constructBody(Injector injector) throws InstantiationException, IllegalAccessException {
        return new SubscriptionStepBody();
    }

    @Override
    public ExecutionPipelineResult initForExecution(WorkflowHost host, PersistenceService persistenceStore, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer)
    {
        if (!executionPointer.eventPublished)
        {
            Object dataRaw = workflow.getData();
            TData data = null;
            if (dataRaw != null) {
                data = (TData) dataRaw;                
            }
            
            if (eventKey != null)
                executionPointer.eventKey = eventKey.apply(data);

            Date date = new Date(0);

            if (effectiveDate != null)
                date = effectiveDate.apply(data);

            executionPointer.eventName = eventName;
            executionPointer.active = false;
            persistenceStore.persistWorkflow(workflow);
            host.subscribeEvent(workflow.getId(), executionPointer.stepId, executionPointer.eventName, executionPointer.eventKey, date);

            return ExecutionPipelineResult.DEFER;
        }
        return ExecutionPipelineResult.NEXT;
    }

    @Override
    public ExecutionPipelineResult beforeExecute(WorkflowHost host, PersistenceService persistenceStore, StepExecutionContext context, ExecutionPointer executionPointer, StepBody body) {
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
