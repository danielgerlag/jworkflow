package com.jworkflow.sample02;

import com.jworkflow.kernel.interfaces.WorkflowHost;
import com.jworkflow.kernel.services.WorkflowModule;
import com.jworkflow.providers.mongodb.MongoPersistenceService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {        
                                
        WorkflowModule.setup();
        //WorkflowModule.setup(MongoPersistenceProvider.class);
        WorkflowHost host = WorkflowModule.getHost();
                
        host.registerWorkflow(DataWorkflow.class);
        
        host.start();
        MyData data = new MyData();
        data.Value1 = 2;
        data.Value2 = 3;
        
        String id = host.startWorkflow("data-workflow", 1, data);
        System.out.println("started wf " + id);
        
        
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();        
        
        System.out.println("shutting down...");
        host.stop();
                        
        
    }
}
