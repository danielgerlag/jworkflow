package com.jworkflow.scratchpad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.jworkflow.providers.aws.DynamoDBLockService;
import net.jworkflow.sample01.HelloWorkflow;
import software.amazon.awssdk.regions.Region;

public class Main {
    public static void main(String[] args) throws Exception {        
        
        Function<TestData, Object> f1 = (o) -> o.id;
        Function<TestData, Object> f2 = (o) -> o.getPid();
        
        //Function<TestData, Object> f3 = TestData::getPid;
        
        
        //Consumer<Integer> f4 = TestData::setPid;
                
        BiConsumer<TestData, Integer> f3 = (o, v) -> o.setPid(v);
                
        
        TestData d1 = new TestData();
        d1.id = 1;
        d1.name = "hi";
        d1.setPid(5);
        
        TestData.class.getField("id").set(d1, 2);
        //TestData.class.getMethod("setPid").set(d1, 8);
        
        f3.accept(d1, 11);
        
        Object v1 = f1.apply(d1);
        Object v2 = f2.apply(d1);
        
        
        System.out.println(String.valueOf(v1));
        System.out.println(String.valueOf(v2));
        
        
        
        
        
    }
    
    
}

