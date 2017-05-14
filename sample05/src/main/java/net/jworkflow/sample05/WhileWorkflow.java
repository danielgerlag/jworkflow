package net.jworkflow.sample05;

import net.jworkflow.sample05.steps.Goodbye;
import net.jworkflow.sample05.steps.Hello;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.sample05.steps.IncrementValue;

public class WhileWorkflow implements Workflow<MyData> {

    @Override
    public String getId() {
        return "while-sample";
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
            .While(data -> data.value1 < 3)
                .run(each -> each
                    .startsWith(IncrementValue.class)
                        .input((step, data) -> step.value = data.value1)
                        .output((step, data) -> data.value1 = step.value)
                )
            .then(Goodbye.class);        
    }    
}
