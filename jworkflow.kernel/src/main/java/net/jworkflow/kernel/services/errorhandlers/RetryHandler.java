package net.jworkflow.kernel.services.errorhandlers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.*;

@Singleton
public class RetryHandler implements StepErrorHandler {

    private final Clock clock;
    
    @Inject
    public RetryHandler(Clock clock) {
        this.clock = clock;
    }
    
    @Override
    public ErrorBehavior getErrorBehavior() {
        return ErrorBehavior.RETRY;
    }

    @Override
    public void handle(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step, ExecutionResultProcessor bubleupHandler) {
        pointer.retryCounter++;        
        pointer.sleepUntil = Date.from(Instant.now(clock).plus(step.getRetryInterval()));
        step.primeForRetry(pointer);
    }
    
}
