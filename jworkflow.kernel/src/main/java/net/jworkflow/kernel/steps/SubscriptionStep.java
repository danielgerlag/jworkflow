package net.jworkflow.kernel.steps;

import com.google.inject.Injector;
import net.jworkflow.kernel.interfaces.StepBody;
import java.util.Date;
import java.util.function.Function;
import net.jworkflow.kernel.models.EventSubscription;
import net.jworkflow.kernel.models.ExecutionPipelineResult;
import net.jworkflow.kernel.models.ExecutionPointer;
import net.jworkflow.kernel.models.StepExecutionContext;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.models.WorkflowExecutorResult;
import net.jworkflow.kernel.models.WorkflowInstance;
import net.jworkflow.kernel.models.WorkflowStep;

public class SubscriptionStep<TData> extends WorkflowStep {
    
    public Function<TData, String> eventKey;
    public String eventName;
    public Function<TData, Date> effectiveDate;

    @Override
    public StepBody constructBody(Injector injector) throws InstantiationException, IllegalAccessException {
        return new SubscriptionStepBody();
    }

    @Override
    public ExecutionPipelineResult initForExecution(WorkflowExecutorResult executorResult, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer)
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
            
            EventSubscription subscription = new EventSubscription();
            subscription.workflowId = workflow.getId();
            subscription.stepId = executionPointer.stepId;
            subscription.eventName = executionPointer.eventName;
            subscription.eventKey = executionPointer.eventKey;
            subscription.subscribeAsOfUtc = date;
            
            executorResult.subscriptions.add(subscription);

            return ExecutionPipelineResult.DEFER;
        }
        return ExecutionPipelineResult.NEXT;
    }

    @Override
    public ExecutionPipelineResult beforeExecute(WorkflowExecutorResult executorResult, StepExecutionContext context, ExecutionPointer executionPointer, StepBody body) {
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
