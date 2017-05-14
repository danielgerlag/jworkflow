package net.jworkflow.sample04;

import net.jworkflow.sample04.steps.Goodbye;
import net.jworkflow.sample04.steps.Hello;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.ExecutionResult;

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
    public void build(WorkflowBuilder builder) {
        
        builder.StartsWith(Hello.class)
                .then(context -> { 
                    return ExecutionResult.next(); 
                })             
                .foreach(data -> new String[]{"blah", "hey", "yo"});
                    //.run(x -> x.)
                //.then(Goodbye.class);
        
    }    
}
