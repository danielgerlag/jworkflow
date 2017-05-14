package net.jworkflow.sample04;

import com.mongodb.client.model.Collation;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import net.jworkflow.sample04.steps.Goodbye;
import net.jworkflow.sample04.steps.Hello;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.ExecutionResult;

public class HelloWorkflow implements Workflow<MyData> {

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
    public void build(WorkflowBuilder<MyData> builder) {
        
        
        builder.startsWith(Hello.class)                          
                .foreach(data -> data.value1)
                    .run(each -> each.startsWith(Hello.class))
                .then(Goodbye.class);
                
                //x.startWith(Hello.class)
        
    }    
}
