package net.jworkflow.kernel.services;
import net.jworkflow.kernel.models.WorkflowDefinition;
import net.jworkflow.kernel.interfaces.WorkflowRegistry;
import net.jworkflow.kernel.interfaces.Workflow;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class WorkflowRegistryImpl implements WorkflowRegistry{
        
    class RegistryEntry {
        private final String id;
        private final int version;
        private final WorkflowDefinition definition;
        
        public RegistryEntry(String id, int version, WorkflowDefinition definition) {
            this.id = id;
            this.version = version;
            this.definition = definition;
        }

        public String getId() {
            return id;
        }

        public int getVersion() {
            return version;
        }
        
        public WorkflowDefinition getDefinition() {
            return definition;
        }
                
        
    }
    
        
    private final List<RegistryEntry> registry;
    private final Logger logger;
    
    @Inject
    public WorkflowRegistryImpl(Logger logger) {
        this.registry = new ArrayList<>();
        this.logger = logger;
    }
    
    @Override
    public void registerWorkflow(Workflow workflow) throws Exception {
        
        //if (registry.containsKey(key))
        //    throw new Exception("already registered");
        
        WorkflowBuilder baseBuilder = new WorkflowBuilder();
        TypedWorkflowBuilder builder = baseBuilder.UseData(workflow.getDataType());        
        
        workflow.build(builder);
        WorkflowDefinition def = builder.build(workflow.getId(), workflow.getVersion());
        
        RegistryEntry entry = new RegistryEntry(workflow.getId(), workflow.getVersion(), def);
        
        registry.add(entry);
        
        logger.log(Level.INFO, String.format("Registered workflow %s %s", workflow.getId(), workflow.getVersion()));
    }

    @Override
    public WorkflowDefinition getDefinition(String workflowId, int version) {
        for (RegistryEntry item : registry) {
            if (item.getId().equals(workflowId) && (item.version == version))
                return item.getDefinition();
        }        
        
        return null;
    }
    
    
}
