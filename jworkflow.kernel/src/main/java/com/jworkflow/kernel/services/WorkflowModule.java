package com.jworkflow.kernel.services;

import com.google.inject.AbstractModule;
import com.jworkflow.kernel.interfaces.WorkflowHost;

public class WorkflowModule extends AbstractModule {
  
    @Override 
    protected void configure() {        
      bind(WorkflowHost.class).to(WorkflowHostImpl.class);      
    }
}
