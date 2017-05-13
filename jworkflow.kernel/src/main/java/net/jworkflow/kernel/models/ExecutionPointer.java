package net.jworkflow.kernel.models;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExecutionPointer {
    public String id;
    public int stepId;
    public boolean active;
    public Duration sleepFor;
    public Object persistenceData;
    public Date startTimeUtc;
    public Date endTimeUtc;
    public String eventName;
    public String eventKey;
    public boolean eventPublished;
    public Object eventData;
    public int retryCounter;
    public String predecessorId;
    public Object contextItem;
    public List<String> children;

    public ExecutionPointer() {
        this.children = new ArrayList<>();
    }
        
}
