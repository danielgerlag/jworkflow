package com.jworkflow.kernel.services.abstractions;

import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.models.WorkflowInstance;
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
    
}
