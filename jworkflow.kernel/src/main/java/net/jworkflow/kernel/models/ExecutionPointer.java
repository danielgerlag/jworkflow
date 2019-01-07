package net.jworkflow.kernel.models;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class ExecutionPointer implements Serializable {
    public String id;
    public int stepId;
    public boolean active;
    public Date sleepUntil;
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
    public Stack<String> callStack;
    public PointerStatus status;

    public ExecutionPointer() {
        this.children = new ArrayList<>();
        this.callStack = new Stack<>();
    }
        
}
