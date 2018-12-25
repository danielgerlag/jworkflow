package net.jworkflow.sample04;

import net.jworkflow.sample04.steps.*;
import net.jworkflow.kernel.interfaces.*;

public class ForeachWorkflow implements Workflow<MyData> {

    @Override
    public String getId() {
        return "foreach-sample";
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
        
        builder
            .startsWith(Hello.class)                          
            .foreach(data -> data.value1)
                .Do(each -> each
                    .startsWith(DoSomething.class))
            .then(Hello.class)
            .foreach(data -> new String[] { "item 1", "item 2", "item 3" })
                .Do(each -> each
                    .startsWith(DoSomething.class))
            .then(Goodbye.class);        
    }    
}
