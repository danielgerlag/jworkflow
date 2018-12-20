package com.jworkflow.scratchpad;

import com.google.gson.Gson;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.function.BiConsumer;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.jworkflow.kernel.interfaces.WorkflowBuilder;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.services.BaseWorkflowBuilder;
import net.jworkflow.kernel.services.DefaultWorkflowBuilder;
import net.jworkflow.sample01.HelloWorkflow;

public class Main {
    public static void main(String[] args) throws Exception {        
     
        
        //System.out.println(Main.class.getName());
        Class cls = Class.forName("com.jworkflow.scratchpad.Main");
        
        
        //HelloWorkflow workflow = new HelloWorkflow();
        //BaseWorkflowBuilder baseBuilder = new BaseWorkflowBuilder();
        //WorkflowBuilder builder = baseBuilder.UseData(workflow.getDataType());        
        
        //workflow.build(builder);
        //WorkflowDefinition def = builder.build(workflow.getId(), workflow.getVersion());
        
        //public interface StepFieldConsumer<TStep extends StepBody, TData> extends BiConsumer<TStep, TData>
        TestData data1 = new TestData(15, "hi");
        TestData data2 = new TestData(2, null);
        
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        
        //new ScriptEngineManager().getEngineByName("nashorn");
        
        Bindings bind = engine.createBindings();
        bind.put("data1", data1);
        bind.put("data2", data2);
        
        Object result = engine.eval("data2.id = data1.id + 100", bind);
        //TestData data3 = (TestData)engine.get("data2");
        //engine.
        System.out.println(result);
        System.out.println(data2.id);
        
        //p.
        
        Object data4 = new TestData(0, null);
        
        data4.getClass().getField("id").set(data4, 88);
        
        System.out.println(((TestData)data4).id);
        
        //BiConsumer<Integer, TestData> cs = ((x, y) -> y.id = x);
        
        //Serializable r = (BiConsumer & Serializable)cs;
        Gson gson = new Gson();        
        String str = gson.toJson(data4);
        System.out.println(str);

        //r.
        
        //int a = 7;
        //TestData b = new TestData(0, null);
        //cs.accept(a, b);
        
        //System.out.println(b.getId());
        
        //Gson gson = new Gson();        
        //String str = gson.toJson(def);
        
        //System.out.println(str);
        
        
        
    }
    
    
}

