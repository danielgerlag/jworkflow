package net.jworkflow.definitionstorage.services;

import net.jworkflow.kernel.models.WorkflowDefinition;

public interface DefinitionLoader {

    WorkflowDefinition loadFromJson(String json) throws Exception;
    
}
