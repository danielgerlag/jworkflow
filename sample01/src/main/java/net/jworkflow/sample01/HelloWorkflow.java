package net.jworkflow.sample01;

import net.jworkflow.sample01.steps.Goodbye;
import net.jworkflow.sample01.steps.Hello;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.ExecutionResult;
import net.jworkflow.kernel.services.*;

public class HelloWorkflow implements Workflow {

    @Override
    public String getId() {
        return "hello";
    }

    @Override
    public Class getDataType() {
        return MyData.class;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void build(TypedWorkflowBuilder builder) {
        
        builder.StartsWith(Hello.class)
                .then(context -> { 
                    return ExecutionResult.next(); 
                })                
                .then(Goodbye.class);
        
    }    
}
