package net.jworkflow.sample04;

import java.util.Arrays;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.kernel.services.WorkflowModule;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {        
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.SEVERE); 
        
        WorkflowModule.setup();
        WorkflowHost host = WorkflowModule.getHost();
                
        host.registerWorkflow(HelloWorkflow.class);
        
        host.start();
        
        MyData data = new MyData();
        data.value1 = new String[] {"abc", "def"};
        
        String id = host.startWorkflow("hello", 1, data);
        System.out.println("started workflow " + id);
        
        
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();        
        
        System.out.println("shutting down...");
        host.stop();
    }
}
