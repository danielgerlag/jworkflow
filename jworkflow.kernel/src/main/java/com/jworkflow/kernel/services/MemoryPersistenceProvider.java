package com.jworkflow.kernel.services;
import com.google.inject.Singleton;
import com.jworkflow.kernel.interfaces.*;
import com.jworkflow.kernel.models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class MemoryPersistenceProvider implements PersistenceProvider {
    
    
    private final List<WorkflowInstance> workflows;
    
    public MemoryPersistenceProvider() {
        workflows = new ArrayList<>();
    }

    @Override
    public synchronized String createNewWorkflow(WorkflowInstance workflow) {        
        workflow.setId(UUID.randomUUID().toString());
        workflows.add(workflow);
        return workflow.getId();        
    }

    @Override
    public synchronized void persistWorkflow(WorkflowInstance workflow) {        
        workflows.removeIf(x -> (x.getId() == null ? workflow.getId() == null : x.getId().equals(workflow.getId())));
        workflows.add(workflow);
    }

    @Override
    public synchronized Iterable<String> getRunnableInstances() {
        ArrayList<String> result = new ArrayList<>();
        workflows.stream().filter(x -> x.getStatus() == WorkflowStatus.RUNNABLE).forEach(item -> {
            result.add(item.getId());
        });        
        return result;
    }

    @Override
    public synchronized WorkflowInstance getWorkflowInstance(String id) {
        Optional<WorkflowInstance> result = workflows.stream().filter(x -> (x.getId() == null ? id == null : x.getId().equals(id))).findFirst();
        if (result.isPresent())
            return result.get();
        else
            return null;        
    }
    
}
