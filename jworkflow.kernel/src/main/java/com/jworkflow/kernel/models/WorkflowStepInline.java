package com.jworkflow.kernel.models;

import com.google.inject.Injector;
import com.jworkflow.kernel.interfaces.StepBody;
import java.util.function.Function;

public class WorkflowStepInline extends WorkflowStep {
        
    
    public class InlineBody implements StepBody {
        
        private final Function<StepExecutionContext, ExecutionResult> body;
        
        public InlineBody(Function<StepExecutionContext, ExecutionResult> body) {
            this.body = body;
        }

        @Override
        public ExecutionResult run(StepExecutionContext context) {
            return body.apply(context);
        }
        
    }
    
    
    private final Function<StepExecutionContext, ExecutionResult> body;
    
    
    public WorkflowStepInline(Function<StepExecutionContext, ExecutionResult> body) {
        super();
        this.body = body;
        setBodyType(InlineBody.class);
    }
    
    @Override
    public StepBody constructBody(Injector injector) throws InstantiationException, IllegalAccessException {
        return new InlineBody(body);
    }
    
}
