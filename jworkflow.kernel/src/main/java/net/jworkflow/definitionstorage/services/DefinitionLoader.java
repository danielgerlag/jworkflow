package net.jworkflow.definitionstorage.services;

import net.jworkflow.kernel.models.WorkflowDefinition;

public interface DefinitionLoader {

    WorkflowDefinition loadDefinition(String json) throws Exception;
    
}
