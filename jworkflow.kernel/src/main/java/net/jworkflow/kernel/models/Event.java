package net.jworkflow.kernel.models;

import java.util.Date;

public class Event {
    public String id;
    public String eventName;
    public String eventKey;
    public Object eventData;
    public Date eventTimeUtc;
    public boolean isProcessed;
}
