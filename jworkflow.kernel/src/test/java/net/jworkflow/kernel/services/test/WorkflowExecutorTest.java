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
import net.jworkflow.kernel.services.DefaultWorkflowExecutor;
import static org.junit.Assert.*;
import org.junit.Test;
import com.google.inject.Injector;
import org.junit.Before;
import static org.mockito.Mockito.*;

public class WorkflowExecutorTest {
    private WorkflowExecutor subject;
    private ExecutionResultProcessor resultProcessor;
    private WorkflowRegistry registry;
    private Injector injector;
    
    @Before
    public void setup() {
        resultProcessor = mock(ExecutionResultProcessor.class);
        registry = mock(WorkflowRegistry.class);
        injector = mock(Injector.class);
        subject = new DefaultWorkflowExecutor(registry, resultProcessor, Clock.systemUTC(), Logger.global, injector);
    }
    
    @Test
    public void should_execute_active_step() throws Exception {
        //arrange   
        StepBody step1Body = mock(StepBody.class);
        when(step1Body.run(any(StepExecutionContext.class))).thenReturn(ExecutionResult.next());
        WorkflowStep step1 = buildFakeStep(step1Body);
        given1StepWorkflow(step1, "Workflow", 1);
        
        ExecutionPointer pointer = new ExecutionPointer();
        pointer.active = true;
        pointer.stepId = 0;

        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowDefintionId("Workflow");
        instance.setVersion(1);
        instance.setStatus(WorkflowStatus.RUNNABLE);
        instance.setNextExecution((long)0);
        instance.setId("001");
        instance.getExecutionPointers().add(pointer);

        //act
        subject.execute(instance);

        //assert
        verify(step1Body).run(any(StepExecutionContext.class));
    }
    
    @Test
    public void should_trigger_step_hooks() throws Exception {
        //arrange            
        StepBody step1Body = mock(StepBody.class);
        when(step1Body.run(any(StepExecutionContext.class))).thenReturn(ExecutionResult.next());        
        WorkflowStep step1 = buildFakeStep(step1Body);
        given1StepWorkflow(step1, "Workflow", 1);
        
        ExecutionPointer pointer = new ExecutionPointer();
        pointer.active = true;
        pointer.stepId = 0;

        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowDefintionId("Workflow");
        instance.setVersion(1);
        instance.setStatus(WorkflowStatus.RUNNABLE);
        instance.setNextExecution((long)0);
        instance.setId("001");
        instance.getExecutionPointers().add(pointer);

        //act
        subject.execute(instance);

        //assert
        verify(step1).initForExecution(any(WorkflowExecutorResult.class), any(WorkflowDefinition.class), any(WorkflowInstance.class), any(ExecutionPointer.class));
        verify(step1).beforeExecute(any(WorkflowExecutorResult.class), any(StepExecutionContext.class), any(ExecutionPointer.class), any(StepBody.class));
        verify(step1).afterExecute(any(WorkflowExecutorResult.class), any(StepExecutionContext.class), any(ExecutionResult.class), any(ExecutionPointer.class));
    }
    
    @Test
    public void should_not_execute_inactive_step() throws Exception {
        //arrange            
        StepBody step1Body = mock(StepBody.class);
        when(step1Body.run(any(StepExecutionContext.class))).thenReturn(ExecutionResult.next());        
        WorkflowStep step1 = buildFakeStep(step1Body);
        given1StepWorkflow(step1, "Workflow", 1);
        
        ExecutionPointer pointer = new ExecutionPointer();
        pointer.active = false;
        pointer.stepId = 0;

        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowDefintionId("Workflow");
        instance.setVersion(1);
        instance.setStatus(WorkflowStatus.RUNNABLE);
        instance.setNextExecution((long)0);
        instance.setId("001");
        instance.getExecutionPointers().add(pointer);
        
        //act
        subject.execute(instance);

        //assert
        verify(step1Body, never()).run(any(StepExecutionContext.class));
    }
    
    @Test
    public void should_map_inputs() throws Exception {
        //arrange
        StepFieldConsumer<StepWithProperties, DataClass> p1 = (step, data) -> step.property1 = data.value1;
        List<StepFieldConsumer> inputs = new ArrayList<>();
        inputs.add(p1);

        StepWithProperties step1Body = mock(StepWithProperties.class);
        when(step1Body.run(any(StepExecutionContext.class))).thenReturn(ExecutionResult.next());        
        WorkflowStep step1 = buildFakeStep(step1Body, inputs, new ArrayList<>());
        given1StepWorkflow(step1, "Workflow", 1);

        ExecutionPointer pointer = new ExecutionPointer();
        pointer.active = true;
        pointer.stepId = 0;
        
        DataClass data = new DataClass();
        data.value1 = 5;

        WorkflowInstance instance = new WorkflowInstance();
        instance.setWorkflowDefintionId("Workflow");
        instance.setVersion(1);
        instance.setStatus(WorkflowStatus.RUNNABLE);
        instance.setNextExecution((long)0);
        instance.setId("001");        
        instance.setData(data);
        instance.getExecutionPointers().add(pointer);        
        
        //act
        subject.execute(instance);

        //assert        
        assertEquals(5, step1Body.property1);
    }
    
    private void given1StepWorkflow(WorkflowStep step1, String id, int version) {
        
        WorkflowDefinition result = new WorkflowDefinition();
        result.setId(id);
        result.setVersion(version);
        result.setDataType(Object.class);
        result.getSteps().add(step1);
        when(registry.getDefinition(id, version)).thenReturn(result);
    }
    
    private WorkflowStep buildFakeStep(StepBody stepBody) throws InstantiationException, IllegalAccessException {
        return buildFakeStep(stepBody, new ArrayList<>(), new ArrayList<>());
    }
    
    private WorkflowStep buildFakeStep(StepBody stepBody, List<StepFieldConsumer> inputs, List<StepFieldConsumer> outputs) throws InstantiationException, IllegalAccessException {
        WorkflowStep result = mock(WorkflowStep.class);
        when(result.getId()).thenReturn(0);
        when(result.getResumeChildrenAfterCompensation()).thenReturn(true);
        when(result.getRevertChildrenAfterCompensation()).thenReturn(false);
        when(result.constructBody(injector)).thenReturn(stepBody);
        when(result.getInputs()).thenReturn(inputs);
        when(result.getOutputs()).thenReturn(outputs);
        when(result.getOutcomes()).thenReturn(new ArrayList<>());
        when(result.initForExecution(any(WorkflowExecutorResult.class), any(WorkflowDefinition.class), any(WorkflowInstance.class), any(ExecutionPointer.class))).thenReturn(ExecutionPipelineResult.NEXT);
        when(result.beforeExecute(any(WorkflowExecutorResult.class), any(StepExecutionContext.class), any(ExecutionPointer.class), any(StepBody.class))).thenReturn(ExecutionPipelineResult.NEXT);        
        
        return result;
    }
    
    private abstract class StepWithProperties implements StepBody {
        int property1;
        int property2;
        int property3;
    }
    
    private class DataClass {
        public int value1;
        public int value2;
        public int value3;
    }
}