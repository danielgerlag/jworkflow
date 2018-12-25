package net.jworkflow.sample06;

import net.jworkflow.sample06.steps.*;
import net.jworkflow.kernel.interfaces.*;

public class IfWorkflow implements Workflow<MyData> {

    @Override
    public String getId() {
        return "if-sample";
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
            .If(data -> data.value1 > 2)
                .Do(then -> then
                        .startsWith(PrintMessage.class)
                            .input((step, data) -> step.message = "Value is greater than 2")
                        .then(PrintMessage.class)
                            .input((step, data) -> step.message = "Doing something...")
                )
            .If(data -> data.value1 == 5)
                .Do(then -> then
                        .startsWith(PrintMessage.class)
                            .input((step, data) -> step.message = "Value is 5")
                        .then(PrintMessage.class)
                            .input((step, data) -> step.message = "Doing something...")
                )
            .If(data -> data.value1 == 3)
                .Do(then -> then
                        .startsWith(PrintMessage.class)
                            .input((step, data) -> step.message = "Value is 3")
                        .then(PrintMessage.class)
                            .input((step, data) -> step.message = "Doing something...")
                )                
            .then(Goodbye.class);        
    }    
}


/*
@Override
    public void build(WorkflowBuilder<Object> builder) {
        
        builder
            .startsWith(Hello.class)                          
            .then(MakeDecision.class)
                .when(0, then -> then
                        .startsWith(PrintMessage.class)
                            .input((step, data) -> step.message = "Doing something with my-outcome")
                        .then(PrintMessage.class)
                            .input((step, data) -> step.message = "Doing something else with my-outcome")
                )
                .when(1, then -> then
                        .startsWith(PrintMessage.class)
                            .input((step, data) -> step.message = "Doing something with your-outcome")
                        .then(PrintMessage.class)
                            .input((step, data) -> step.message = "Doing something else with your-outcome")
                )
            .then(Goodbye.class);        
    }    
*/
