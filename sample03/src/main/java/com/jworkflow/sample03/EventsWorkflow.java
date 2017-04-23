package com.jworkflow.sample03;

import com.jworkflow.kernel.interfaces.Workflow;
import com.jworkflow.kernel.services.TypedWorkflowBuilder;
import com.jworkflow.sample03.steps.*;
import java.util.Date;

public class EventsWorkflow implements Workflow<MyData> {

    @Override
    public String getId() {
        return "events-workflow";
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
    public void build(TypedWorkflowBuilder<MyData> builder) {
        
        builder
                .StartsWith(Hello.class)
                .waitFor("myEvent", x -> "1", x -> new Date())
                    .output((step, data) -> data.value1 = step.eventData)
                .then(DisplayAnswer.class)
                    .input((step, data) -> step.answer = data.value1);
    }
    
}
