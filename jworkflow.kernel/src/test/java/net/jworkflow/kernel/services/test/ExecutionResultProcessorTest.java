package net.jworkflow.kernel.services.test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.*;
import net.jworkflow.kernel.services.DefaultExecutionResultProcessor;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.*;

public class ExecutionResultProcessorTest {
    
    private ExecutionResultProcessor subject;
    private ExecutionPointerFactory pointerFactory;
    private Set<StepErrorHandler> errorHandlers;
    
    @Before
    public void setup() {
        pointerFactory = mock(ExecutionPointerFactory.class);
        errorHandlers = new HashSet<>();
        subject = new DefaultExecutionResultProcessor(pointerFactory, errorHandlers, Clock.systemUTC(), Logger.global);
    }
    
    
    @Test
    public void should_advance_workflow() {
        //arrange
        WorkflowDefinition definition = new WorkflowDefinition();
        ExecutionPointer pointer1 = new ExecutionPointer();
        pointer1.id = "1";
        pointer1.active = true;
        pointer1.stepId = 0;
        pointer1.status = PointerStatus.Running;
        
        ExecutionPointer pointer2 = new ExecutionPointer();
        
        StepOutcome outcome = new StepOutcome();
        outcome.setNextStep(1);
        List<StepOutcome> outcomeList = new ArrayList<>();
        outcomeList.add(outcome);
        
        WorkflowStep step = mock(WorkflowStep.class);
        WorkflowExecutorResult workflowResult = new WorkflowExecutorResult();
        WorkflowInstance instance = givenWorkflow(pointer1);
        ExecutionResult result = ExecutionResult.next();

        when(step.getOutcomes()).thenReturn(outcomeList);
        when(pointerFactory.buildNextPointer(definition, pointer1, outcome)).thenReturn(pointer2);
        
        //act
        subject.processExecutionResult(instance, definition, pointer1, step, result, workflowResult);

        //assert
        assertFalse(pointer1.active);
        assertEquals(PointerStatus.Complete, pointer1.status);
        assertNotNull(pointer1.endTimeUtc);
        verify(pointerFactory).buildNextPointer(definition, pointer1, outcome);        
        assertTrue(instance.getExecutionPointers().stream().anyMatch(x -> x == pointer2));        
    }
    
    @Test
    public void should_set_persistence_data() {
        //arrange
        Object persistenceData = new Object();
        WorkflowDefinition definition = new WorkflowDefinition();
        ExecutionPointer pointer = new ExecutionPointer();
        pointer.id = "1";
        pointer.active = true;
        pointer.stepId = 0;
        pointer.status = PointerStatus.Running;
        
        WorkflowStep step = mock(WorkflowStep.class);
        WorkflowExecutorResult workflowResult = new WorkflowExecutorResult();
        WorkflowInstance instance = givenWorkflow(pointer);
        ExecutionResult result = ExecutionResult.persist(persistenceData);

        //act
        subject.processExecutionResult(instance, definition, pointer, step, result, workflowResult);

        //assert
        assertSame(persistenceData, pointer.persistenceData);
    }
 
    @Test
    public void should_subscribe_to_event() {
        //arrange
        WorkflowDefinition definition = new WorkflowDefinition();
        ExecutionPointer pointer = new ExecutionPointer();
        pointer.id = "1";
        pointer.active = true;
        pointer.stepId = 0;
        pointer.status = PointerStatus.Running;
        WorkflowStep step = mock(WorkflowStep.class);
        WorkflowExecutorResult workflowResult = new WorkflowExecutorResult();
        WorkflowInstance instance = givenWorkflow(pointer);
        ExecutionResult result = ExecutionResult.waitForEvent("Event", "Key", Date.from(Instant.now()));

        //act
        subject.processExecutionResult(instance, definition, pointer, step, result, workflowResult);

        //assert
        assertEquals(PointerStatus.WaitingForEvent, pointer.status);
        assertFalse(pointer.active);
        assertEquals("Event", pointer.eventName);
        assertEquals("Key", pointer.eventKey);
        assertTrue(workflowResult.subscriptions.stream().anyMatch(x -> x.stepId == pointer.stepId && x.eventName == "Event" && x.eventKey == "Key"));        
    }
    
    @Test
    public void should_select_correct_outcomes() {
        //arrange            
        WorkflowDefinition definition = new WorkflowDefinition();
        ExecutionPointer pointer1 = new ExecutionPointer();
        pointer1.id = "1";
        pointer1.active = true;
        pointer1.stepId = 0;
        pointer1.status = PointerStatus.Running;        
        ExecutionPointer pointer2 = new ExecutionPointer();
        pointer2.id = "2";
        ExecutionPointer pointer3 = new ExecutionPointer();
        pointer3.id = "3";
        StepOutcome outcome1 = new StepOutcome();
        outcome1.setNextStep(1);
        outcome1.setValue(10);
        StepOutcome outcome2 = new StepOutcome();
        outcome2.setNextStep(2);
        outcome2.setValue(20);
        WorkflowStep step = mock(WorkflowStep.class);
        WorkflowExecutorResult workflowResult = new WorkflowExecutorResult();
        WorkflowInstance instance = givenWorkflow(pointer1);
        ExecutionResult result = ExecutionResult.outcome(20);
        List<StepOutcome> outcomeList = new ArrayList<>();
        outcomeList.add(outcome1);
        outcomeList.add(outcome2);
        
        when(step.getOutcomes()).thenReturn(outcomeList);
        when(pointerFactory.buildNextPointer(definition, pointer1, outcome1)).thenReturn(pointer2);
        when(pointerFactory.buildNextPointer(definition, pointer1, outcome2)).thenReturn(pointer3);        

        //act
        subject.processExecutionResult(instance, definition, pointer1, step, result, workflowResult);

        //assert
        assertFalse(pointer1.active);
        assertEquals(PointerStatus.Complete, pointer1.status);
        assertNotNull(pointer1.endTimeUtc);
        assertTrue(instance.getExecutionPointers().stream().allMatch(x -> x != pointer2));
        assertTrue(instance.getExecutionPointers().stream().anyMatch(x -> x != pointer3));
    }
    
    @Test
    public void should_sleep_pointer() {
        //arrange
        Object persistenceData = new Object();
        WorkflowDefinition definition = new WorkflowDefinition();
        ExecutionPointer pointer = new ExecutionPointer();
        pointer.id = "1";
        pointer.active = true;
        pointer.stepId = 0;
        pointer.status = PointerStatus.Running;        
        WorkflowStep step = mock(WorkflowStep.class);
        WorkflowExecutorResult workflowResult = new WorkflowExecutorResult();
        WorkflowInstance instance = givenWorkflow(pointer);
        ExecutionResult result = ExecutionResult.sleep(Duration.ofMinutes(5), persistenceData);

        //act
        subject.processExecutionResult(instance, definition, pointer, step, result, workflowResult);

        //assert
        assertEquals(PointerStatus.Sleeping, pointer.status);
        assertNotNull(pointer.sleepUntil);
    }
    
    @Test
    public void should_branch_children() {
        //arrange
        int branch = 10;
        int child = 2;
        WorkflowDefinition definition = new WorkflowDefinition();
        ExecutionPointer pointer = new ExecutionPointer();
        pointer.id = "1";
        pointer.active = true;
        pointer.stepId = 0;        
        ExecutionPointer childPointer = new ExecutionPointer();
        WorkflowStep step = mock(WorkflowStep.class);
        WorkflowExecutorResult workflowResult = new WorkflowExecutorResult();
        WorkflowInstance instance = givenWorkflow(pointer);
        Object[] branchArray = new Object[1];
        branchArray[0] = branch;
        ExecutionResult result = ExecutionResult.branch(branchArray, null);
        Collection<Integer> childList = new ArrayList<>();
        childList.add(child);

        when(step.getChildren()).thenReturn(childList);
        when(pointerFactory.buildChildPointer(definition, pointer, child, branch)).thenReturn(childPointer);

        //act
        subject.processExecutionResult(instance, definition, pointer, step, result, workflowResult);

        //assert
        verify(pointerFactory).buildChildPointer(definition, pointer, child, branch);
        assertTrue(instance.getExecutionPointers().stream().anyMatch(x -> x == childPointer));
    }
    
    private static WorkflowInstance givenWorkflow(ExecutionPointer pointer) {
        WorkflowInstance result = new WorkflowInstance();        
        result.setStatus(WorkflowStatus.RUNNABLE);
        result.getExecutionPointers().add(pointer);
        return result;
    }
}
