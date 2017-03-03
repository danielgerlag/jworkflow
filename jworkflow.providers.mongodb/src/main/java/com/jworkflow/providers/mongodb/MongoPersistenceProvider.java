package com.jworkflow.providers.mongodb;

import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.models.WorkflowInstance;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

public class MongoPersistenceProvider implements PersistenceProvider {

    MongoOperations mongoOperation;
    
    public MongoPersistenceProvider(String uri, String db) {
        
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
        mongoOperation = (MongoOperations)ctx.getBean("mongoTemplate");        
    }
    
    
    @Override
    public String createNewWorkflow(WorkflowInstance workflow) {
        mongoOperation.save(workflow);
        return workflow.getId();
    }

    @Override
    public void persistWorkflow(WorkflowInstance workflow) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<String> getRunnableInstances() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public WorkflowInstance getWorkflowInstance(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
