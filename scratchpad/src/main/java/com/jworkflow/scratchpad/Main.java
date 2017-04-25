package com.jworkflow.scratchpad;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws Exception {        
     
        
        Date dt1 = new Date();                        
        Date dt2 = Date.from(Instant.now());
       // Calendar.getInstance().
        //dt1.
        
        System.out.println("dt1 = " + dt1);
        System.out.println("dt2 = " + dt2);
        
    }
}

