package com.jworkflow.sample03;

import com.jworkflow.kernel.interfaces.WorkflowHost;
import com.jworkflow.kernel.services.WorkflowModule;
import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {        
                                
        WorkflowModule.setup();
        //WorkflowModule.setup(MongoPersistenceProvider.class);
        WorkflowHost host = WorkflowModule.getHost();
                
        host.registerWorkflow(EventsWorkflow.class);
        
        host.start();
        MyData data = new MyData();        
        
        String id = host.startWorkflow("events-workflow", 1, data);
        System.out.println("started wf " + id);
        
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter value to publish");
        String inputData = scanner.nextLine();
        
        host.publishEvent("myEvent", "1", inputData, new Date());
        
        scanner.nextLine();
        System.out.println("shutting down...");
        host.stop();
                        
        
    }
}