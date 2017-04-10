package com.jworkflow.sample02;

import com.jworkflow.sample02.steps.DisplayAnswer;
import com.jworkflow.sample02.steps.AddNumbers;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.services.*;

public class DataWorkflow implements Workflow<MyData> {

    @Override
    public String getId() {
        return "data-workflow";
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
                .StartsWith(AddNumbers.class)  
                    .input((step, data) -> step.Number1 = data.Value1)
                    .input((step, data) -> step.Number2 = data.Value2)
                    .output((step, data) -> data.Value3 = step.Answer)
                .then(DisplayAnswer.class)
                    .input((step, data) -> step.Answer = data.Value3);
    }
    
}
