package net.jworkflow.kernel.steps;

import com.google.inject.Injector;
import java.util.AbstractCollection;
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

public class ForeachStep<TData> extends WorkflowStep {
    
    public Function<TData, AbstractCollection> collection;

    @Override
    public StepBody constructBody(Injector injector) throws InstantiationException, IllegalAccessException {
        return new Foreach();
    }

    @Override
    public ExecutionPipelineResult initForExecution(WorkflowExecutorResult executorResult, WorkflowDefinition defintion, WorkflowInstance workflow, ExecutionPointer executionPointer)
    {
        
        return ExecutionPipelineResult.NEXT;
    }

    @Override
    public ExecutionPipelineResult beforeExecute(WorkflowExecutorResult executorResult, StepExecutionContext context, ExecutionPointer executionPointer, StepBody body) {
        
        if (body instanceof Foreach) {
            Foreach feBody = (Foreach)body;
            feBody.collection = (Function<Object, AbstractCollection>) collection;
        }                

        return ExecutionPipelineResult.NEXT;
    }
}
