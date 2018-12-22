/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jworkflow.scratchpad;

import java.util.Properties;

/**
 *
 * @author Daniel.Gerlag
 */
public class TestData {
        public int id;
        public String name;
        public Properties props;
        
        
        public TestData(int id,String name) {
            this.id = id;
            this.name = name;
            props = new Properties();
            props.put("p1", "hi there");
            props.put("p2", "bye");
            
        }
    }