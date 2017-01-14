package com.jworkflow.sample01;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jworkflow.kernel.interfaces.WorkflowHost;

public class Main {
    public static void main(String[] args) {        
                
                
        AbstractModule module = new com.jworkflow.kernel.services.WorkflowModule();        
        Injector injector = Guice.createInjector(module);        

        WorkflowHost host = injector.getInstance(WorkflowHost.class);
                
        String id = host.startWorkflow();
        System.out.println(id);
        
    }
}
