package net.jworkflow.sample03;

import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.WorkflowModule;
import net.jworkflow.providers.mongodb.MongoPersistenceService;
import java.util.Date;
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
        
        host.registerWorkflow(EventsWorkflow.class);
        
        host.start();
        MyData data = new MyData();        
        
        String id = host.startWorkflow("events-workflow", 1, data);
        System.out.println("started workflow " + id);        
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter value to publish");
        String inputData = scanner.nextLine();
        
        host.publishEvent("myEvent", "1", inputData, new Date());
        
        scanner.nextLine();
        System.out.println("shutting down...");
        host.stop();                        
        
    }
}