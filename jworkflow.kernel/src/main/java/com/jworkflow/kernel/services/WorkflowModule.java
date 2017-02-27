package com.jworkflow.kernel.services;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jworkflow.kernel.interfaces.*;

public class WorkflowModule extends AbstractModule {
  
    @Override 
    protected void configure() {        
      bind(WorkflowHost.class).to(WorkflowHostImpl.class);
      bind(WorkflowExecutor.class).to(WorkflowExecutorImpl.class);
      bind(WorkflowRegistry.class).to(WorkflowRegistryImpl.class);
      
      //
      bind(PersistenceProvider.class).to(MemoryPersistenceProvider.class);
      bind(LockProvider.class).to(SingleNodeLockProvider.class);
      bind(QueueProvider.class).to(SingleNodeQueueProvider.class);
      
    }
    
    
    private static Injector injector;
    
    public static void setup() {
        AbstractModule module = new WorkflowModule();        
        injector = Guice.createInjector(module);        
    }
    
    public static WorkflowHost getHost() {
        if (injector != null)
            return injector.getInstance(WorkflowHost.class);        
        return null;
    }
}
