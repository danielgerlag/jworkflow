package com.jworkflow.kernel.models;

import java.util.Date;

public class EventSubscription {
    public String id;
    public String workflowId;
    public int stepId;
    public String eventName;
    public String eventKey;
    public Date subscribeAsOfUtc;

}
