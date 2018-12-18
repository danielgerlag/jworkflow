package com.jworkflow.scratchpad;

import com.google.gson.Gson;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.services.BaseWorkflowBuilder;
import net.jworkflow.kernel.services.DefaultWorkflowBuilder;
import net.jworkflow.sample01.HelloWorkflow;

public class Main {
    public static void main(String[] args) throws Exception {        
     
        HelloWorkflow workflow = new HelloWorkflow();
        BaseWorkflowBuilder baseBuilder = new BaseWorkflowBuilder();
        WorkflowBuilder builder = baseBuilder.UseData(workflow.getDataType());        
        
        workflow.build(builder);
        WorkflowDefinition def = builder.build(workflow.getId(), workflow.getVersion());
        
        Gson gson = new Gson();
        
        String str = gson.toJson(def);
        
        System.out.println(str);
        
        
        
    }
    
    static class TestData {
        public int id;
        public String name;
        
        public int getId() {
            return id;
        }
                
        
        public TestData(int id,String name) {
            this.id = id;
            this.name = name;
        }
    }
}

