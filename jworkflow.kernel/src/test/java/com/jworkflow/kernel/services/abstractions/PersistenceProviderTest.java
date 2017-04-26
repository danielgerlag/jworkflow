package com.jworkflow.kernel.services.abstractions;

import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.models.Event;
import com.jworkflow.kernel.models.WorkflowInstance;
import com.jworkflow.kernel.models.WorkflowStatus;
import java.util.Date;
import java.util.stream.StreamSupport;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public abstract class PersistenceProviderTest {
    
    public abstract PersistenceProvider createProvider();
    
    @Test
    public void createNewWorkflow() {
        //arrange
        PersistenceProvider provider = createProvider();
        WorkflowInstance wf = new WorkflowInstance();
        wf.setDescription("wf1");
        
        //act
        String id = provider.createNewWorkflow(wf);
        
        //assert        
        assertNotNull(id);
        WorkflowInstance wf2 = provider.getWorkflowInstance(id);
        assertNotNull(wf2);
    }
    
    @Test
    public void persistWorkflow() {
        //arrange
        PersistenceProvider provider = createProvider();
        WorkflowInstance wf1 = new WorkflowInstance();
        wf1.setDescription("wf1");
        String id = provider.createNewWorkflow(wf1);
        
        WorkflowInstance wf2 = new WorkflowInstance();
        wf2.setId(id);
        wf2.setDescription("wf2");
        
        //act
        provider.persistWorkflow(wf2);
        
        //assert        
        WorkflowInstance wf3 = provider.getWorkflowInstance(id);
        assertNotNull(wf3);
        assertEquals(wf3, wf2);
    }
    
    @Test
    public void createEvent() {
        //arrange
        PersistenceProvider provider = createProvider();
        Event evt = new Event();
        evt.eventName = "test";
        evt.eventKey = "1";
        evt.eventData = makeTestData();
                
        //act
        String id = provider.createEvent(evt);
        
        //assert        
        assertNotNull(id);
        Event evt2 = provider.getEvent(id);
        assertNotNull(evt2);
    }
    
    @Test
    public void getRunnableIntances() {
        //arrange
        PersistenceProvider provider = createProvider();        
        String wf1 = provider.createNewWorkflow(makeTestWorkflow("wf1", (long)0, WorkflowStatus.RUNNABLE));
        String wf2 = provider.createNewWorkflow(makeTestWorkflow("wf2", new Date().getTime(), WorkflowStatus.RUNNABLE));
        String wf3 = provider.createNewWorkflow(makeTestWorkflow("wf3", (long)0, WorkflowStatus.COMPLETE));
        String wf4 = provider.createNewWorkflow(makeTestWorkflow("wf4", (long)0, WorkflowStatus.SUSPENDED));
        String wf5 = provider.createNewWorkflow(makeTestWorkflow("wf5", null, WorkflowStatus.RUNNABLE));
                        
        //act
        Iterable<String> result = provider.getRunnableInstances();
        
        //assert                       
        Assert.assertTrue(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(wf1)));
        Assert.assertTrue(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(wf2)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(wf3)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(wf4)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(wf5)));
    }
    
    @Test
    public void getRunnableEvents() {
        //arrange
        PersistenceProvider provider = createProvider();        
        String evt1 = provider.createEvent(makeTestEvent("event", "1", false, new Date(new Date().getTime() - 100000)));
        String evt2 = provider.createEvent(makeTestEvent("event", "2", true, new Date(new Date().getTime() - 100000)));
        String evt3 = provider.createEvent(makeTestEvent("event", "3", false, new Date(new Date().getTime() + 100000)));
        String evt4 = provider.createEvent(makeTestEvent("event", "4", true, new Date(new Date().getTime() + 100000)));
        String evt5 = provider.createEvent(makeTestEvent("event", "5", false, new Date(new Date().getTime() - 100000)));
                        
        //act
        Iterable<String> result = provider.getRunnableEvents();
        
        //assert                       
        Assert.assertTrue(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt1)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt2)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt3)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt4)));
        Assert.assertTrue(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt5)));
    }
    
    @Test
    public void getEvents() {
        //arrange
        PersistenceProvider provider = createProvider();        
        String evt1 = provider.createEvent(makeTestEvent("event", "1", false, new Date(new Date().getTime() - 100000)));
        String evt2 = provider.createEvent(makeTestEvent("event", "1", false, new Date(new Date().getTime() + 100000)));
        String evt3 = provider.createEvent(makeTestEvent("event2", "1", false, new Date(new Date().getTime() + 100000)));
        String evt4 = provider.createEvent(makeTestEvent("event", "2", true, new Date(new Date().getTime() + 100000)));
        String evt5 = provider.createEvent(makeTestEvent("event2", "2", false, new Date(new Date().getTime() - 100000)));
                        
        //act
        Iterable<String> result = provider.getEvents("event", "1", new Date());
        
        //assert                       
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt1)));
        Assert.assertTrue(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt2)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt3)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt4)));
        Assert.assertFalse(StreamSupport.stream(result.spliterator(), true).anyMatch(x -> x.equals(evt5)));
    }
    
    private TestDataClass makeTestData() {
        TestDataClass result = new TestDataClass();
        result.Value1 = 2;
        result.Value2 = 3;
        return result;
    }
    
    private WorkflowInstance makeTestWorkflow(String description, Long nextExecution, WorkflowStatus status) {
        WorkflowInstance wf = new WorkflowInstance();
        wf.setDescription(description);
        wf.setNextExecution(nextExecution);
        wf.setStatus(status);
        return wf;
    }
    
    private Event makeTestEvent(String eventName, String eventKey, boolean isProcessed, Date eventTime) {
        Event evt = new Event();
        evt.eventName = eventName;
        evt.eventKey = eventKey;
        evt.isProcessed = isProcessed;
        evt.eventTimeUtc = eventTime;
        return evt;
    }
}
