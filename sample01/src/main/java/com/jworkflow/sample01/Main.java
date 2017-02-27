package com.jworkflow.sample01;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jworkflow.kernel.interfaces.WorkflowHost;
import com.jworkflow.kernel.interfaces.WorkflowRegistry;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {        
                
                
        AbstractModule module = new com.jworkflow.kernel.services.WorkflowModule();        
        Injector injector = Guice.createInjector(module);        

        WorkflowHost host = injector.getInstance(WorkflowHost.class);
        WorkflowRegistry registry = injector.getInstance(WorkflowRegistry.class);
        
        registry.registerWorkflow(new HelloWorkflow());
        
        host.start();
        
        String id = host.startWorkflow("hello", 1, null);
        System.out.println("started wf " + id);
        
        
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();        
        
        System.out.println("shutting down...");
        host.stop();
                        
        
    }
}
