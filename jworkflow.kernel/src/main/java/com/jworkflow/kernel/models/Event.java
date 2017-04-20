package com.jworkflow.kernel.models;

import java.util.Date;

public class Event {
    public String id;
    public String eventName;
    public String eventKey;
    public Object eventData;
    public Date eventTime;
    public boolean isProcessed;
}
