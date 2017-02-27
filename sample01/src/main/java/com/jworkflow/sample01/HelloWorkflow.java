package com.jworkflow.sample01;

import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.services.*;

public class HelloWorkflow implements Workflow {

    @Override
    public String getId() {
        return "hello";
    }

    @Override
    public Class getDataType() {
        return null;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void build(TypedWorkflowBuilder builder) {
        builder.StartsWith(Hello.class)
                .then(Goodbye.class);
        
    }
    
}
