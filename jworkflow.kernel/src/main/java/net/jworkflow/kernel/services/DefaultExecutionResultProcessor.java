package net.jworkflow.kernel.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.*;

@Singleton
public class DefaultExecutionResultProcessor implements ExecutionResultProcessor {

    private final ExecutionPointerFactory pointerFactory;
    private final Set<StepErrorHandler> errorHandlers;
    private final Clock clock;
    private final Logger logger;
    
    @Inject
    public DefaultExecutionResultProcessor(ExecutionPointerFactory pointerFactory, Set<StepErrorHandler> errorHandlers, Clock clock, Logger logger) {
        this.pointerFactory = pointerFactory;
        this.errorHandlers = errorHandlers;
        this.clock = clock;
        this.logger = logger;
    }
        
    @Override
    public void processExecutionResult(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step, ExecutionResult result, WorkflowExecutorResult workflowResult) {
        pointer.persistenceData = result.getPersistenceData();
        //pointer. = result.OutcomeValue;
        if (result.getSleepFor() != null) {
            pointer.sleepUntil = Date.from(Instant.now(clock).plus(result.getSleepFor()));
            pointer.status = PointerStatus.Sleeping;
        }
        
        if (result.getEventName() != null) {
            pointer.eventName = result.getEventName();
            pointer.eventKey = result.getEventKey();
            pointer.active = false;
            pointer.status = PointerStatus.WaitingForEvent;

            
            EventSubscription sub = new EventSubscription();
            sub.workflowId = workflow.getId();
            sub.stepId = pointer.stepId;
            sub.eventName = pointer.eventName;
            sub.eventKey = pointer.eventKey;
            sub.subscribeAsOfUtc = result.getEventAsOf();
            workflowResult.subscriptions.add(sub);
        }

        if (result.isProceed()) {
            pointer.active = false;
            pointer.endTimeUtc = Date.from(Instant.now(clock));
            pointer.status = PointerStatus.Complete;
            
            StepOutcome[] outcomes = step.getOutcomes().stream()
                    .filter(x -> x.getValue() == result.getOutcomeValue())
                    .toArray(StepOutcome[]::new);
            
            for (StepOutcome outcome : outcomes) {
                workflow.getExecutionPointers().add(pointerFactory.buildNextPointer(def, pointer, outcome));
            }
        }
        else
        {
            for (Object branchValue: result.getBranches()) {
                step.getChildren().stream()
                        .map((childId) -> pointerFactory.buildChildPointer(def, pointer, childId, branchValue))
                        .forEachOrdered((newPointer) -> {
                            workflow.getExecutionPointers().add(newPointer);
                        });
            }
        }
    }
    
    @Override
    public void handleStepException(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer pointer, WorkflowStep step) {
        pointer.status = PointerStatus.Failed;            
        
        Queue<ExecutionPointer> queue = new LinkedList<>();
        queue.add(pointer);
        
        while (!queue.isEmpty()) {
            ExecutionPointer exceptionPointer = queue.remove();
            WorkflowStep exceptionStep = def.findStep(exceptionPointer.stepId);
            
            Integer compensatingStepId = findScopeCompensationStepId(workflow, def, exceptionPointer);
            ErrorBehavior errorOption = exceptionStep.getRetryBehavior();
            if (errorOption == null) {
                if (compensatingStepId != null) {
                    errorOption = ErrorBehavior.COMPENSATE;
                }
                else {
                    errorOption = ErrorBehavior.RETRY; //def.DefaultErrorBehavior
                }
            }

            for (StepErrorHandler handler: errorHandlers) {
                if (handler.getErrorBehavior() == errorOption)
                    handler.handle(workflow, def, exceptionPointer, exceptionStep, queue);
            }
        }
    }    
    
    private Integer findScopeCompensationStepId(WorkflowInstance workflow, WorkflowDefinition def, ExecutionPointer currentPointer) {
        Stack<String> scope = (Stack<String>)currentPointer.callStack.clone();
        scope.push(currentPointer.id);

        while (!scope.empty()) {
            String pointerId = scope.pop();
            ExecutionPointer pointer = workflow.getExecutionPointers().findById(pointerId);
            WorkflowStep step = def.findStep(pointer.stepId);
            if (step.getCompensationStepId() != null)
                return step.getCompensationStepId();
        }

        return null;
    }
}
