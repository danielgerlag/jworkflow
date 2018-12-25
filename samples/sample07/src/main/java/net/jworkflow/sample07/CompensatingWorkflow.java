package net.jworkflow.sample07;

import java.time.Duration;
import net.jworkflow.sample07.steps.*;
import net.jworkflow.kernel.interfaces.*;
import net.jworkflow.kernel.models.ErrorBehavior;

public class CompensatingWorkflow implements Workflow<Object> {

    @Override
    public String getId() {
        return "saga-sample";
    }

    @Override
    public Class getDataType() {
        return Object.class;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public void build(WorkflowBuilder<Object> builder) {        
        builder
            .startsWithAction(context -> System.out.println("begin"))
            .saga(saga -> saga
                .startsWith(Task1.class)
                    .compensateWith(UndoTask1.class)
                .then(Task2.class)
                    .compensateWith(UndoTask2.class)
                .then(Task3.class)
                    .compensateWith(UndoTask3.class)
            )
            .thenAction(context -> System.out.println("end")); 
    }    
}