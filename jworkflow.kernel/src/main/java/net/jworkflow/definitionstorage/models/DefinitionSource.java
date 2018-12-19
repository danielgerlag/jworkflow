package net.jworkflow.definitionstorage.models;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import net.jworkflow.kernel.models.ErrorBehavior;

public class DefinitionSource {

    public String id;
    
    public int version;
    
    public String description;
    
    public String dataType;

    public ErrorBehavior defaultErrorBehavior;

    public Duration defaultErrorRetryInterval;

    public List<StepSource> steps = new ArrayList<>();
}
