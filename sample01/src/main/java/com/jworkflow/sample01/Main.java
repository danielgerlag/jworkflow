package com.jworkflow.sample01;

import com.jworkflow.kernel.interfaces.WorkflowHost;
import com.jworkflow.kernel.services.WorkflowModule;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {        
                                
        WorkflowModule.setup();
        WorkflowHost host = WorkflowModule.getHost();
                
        host.registerWorkflow(new HelloWorkflow());
        
        host.start();
        
        String id = host.startWorkflow("hello", 1, null);
        System.out.println("started wf " + id);
        
        
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();        
        
        System.out.println("shutting down...");
        host.stop();
                        
        
    }
}
