package com.jworkflow.kernel.services;

import com.google.inject.AbstractModule;
import com.jworkflow.kernel.interfaces.*;
import java.util.logging.Logger;

public class WorkflowModule extends AbstractModule {
  
    @Override 
    protected void configure() {        
      bind(WorkflowHost.class).to(WorkflowHostImpl.class);
      bind(WorkflowExecutor.class).to(WorkflowExecutorImpl.class);
      bind(WorkflowRegistry.class).to(WorkflowRegistryImpl.class);
      
      bind(PollThread.class).to(PollThread.class);
      bind(WorkflowThread.class).to(WorkflowThread.class);
      
      //
      bind(PersistenceProvider.class).to(MemoryPersistenceProvider.class);
      bind(LockProvider.class).to(SingleNodeLockProvider.class);
      bind(QueueProvider.class).to(SingleNodeQueueProvider.class);
      
    }
}
