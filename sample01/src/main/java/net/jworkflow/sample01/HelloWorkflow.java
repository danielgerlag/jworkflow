package net.jworkflow.sample01;

import net.jworkflow.sample01.steps.Goodbye;
import net.jworkflow.sample01.steps.Hello;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.ExecutionResult;

public class HelloWorkflow implements Workflow {

    @Override
    public String getId() {
        return "hello";
    }
    
    @Override
    public int getVersion() {
        return 1;
    }
    
    @Override
    public Class getDataType() {
        return Object.class;
    }

    @Override
    public void build(WorkflowBuilder builder) {
        
        builder
            .startsWith(Hello.class)                
            .then(context -> { 
                System.out.println("Doing something inline...");
                return ExecutionResult.next(); 
            })                
            .then(Goodbye.class);
        
    }
}
