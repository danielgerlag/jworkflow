package net.jworkflow.sample02;

import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.kernel.services.WorkflowModule;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {        
        
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.SEVERE); 
                                
        WorkflowModule module = new WorkflowModule();
        module.build();
        WorkflowHost host = module.getHost();
        
        host.registerWorkflow(DataWorkflow.class);
        
        host.start();
        MyData data = new MyData();
        data.value1 = 2;
        data.value2 = 3;
        
        String id = host.startWorkflow("data-workflow", 1, data);
        System.out.println("started workflow " + id);
        
        
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();        
        
        System.out.println("shutting down...");
        host.stop();
                        
        
    }
}
