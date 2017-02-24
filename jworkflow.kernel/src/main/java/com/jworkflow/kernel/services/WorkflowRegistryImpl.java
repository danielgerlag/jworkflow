package com.jworkflow.kernel.services;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.util.HashMap;
import java.util.Map;

public class WorkflowRegistryImpl implements WorkflowRegistry{
    
    class RegistryKey {
        private final String id;
        private final int version;
        
        public RegistryKey(String id, int version) {
            this.id = id;
            this.version = version;
        }

        public String getId() {
            return id;
        }

        public int getVersion() {
            return version;
        }
        
    }
    
        
    private final Map<RegistryKey, WorkflowDefinition> registry;
    
    public WorkflowRegistryImpl() {
        this.registry = new HashMap<>();        
    }
    
    @Override
    public void registerWorkflow(Workflow workflow) throws Exception {
        RegistryKey key = new RegistryKey(workflow.getId(), workflow.getVersion());
        if (registry.containsKey(key))
            throw new Exception("already registered");
        
        WorkflowBuilder builder = null;
        
        workflow.build(builder);
        WorkflowDefinition def = builder.build(workflow.getId(), workflow.getVersion());
        registry.put(key, def);
    }

    @Override
    public WorkflowDefinition getDefinition(String workflowId, int version) {
        RegistryKey key = new RegistryKey(workflowId, version);
        return registry.get(key);
    }
    
    
}
