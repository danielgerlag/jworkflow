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
import net.jworkflow.kernel.builders.BaseWorkflowBuilder;
import net.jworkflow.kernel.builders.DefaultWorkflowBuilder;
import net.jworkflow.providers.aws.DynamoDBLockService;
import net.jworkflow.sample01.HelloWorkflow;
import software.amazon.awssdk.regions.Region;

public class Main {
    public static void main(String[] args) throws Exception {        
        
        DynamoDBLockService dlm = new DynamoDBLockService(Region.US_WEST_1, "table4");
        System.out.println("starting...");
        dlm.start();
        System.out.println("started");
        
        
        boolean lock1 = dlm.acquireLock("lock1");
        dlm.releaseLock("lock1");
        System.out.println(lock1);
        
        dlm.stop();
        
        
    }
    
    
}

