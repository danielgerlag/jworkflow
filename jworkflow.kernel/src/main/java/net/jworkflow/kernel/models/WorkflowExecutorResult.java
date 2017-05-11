package net.jworkflow.kernel.models;

import java.util.ArrayList;
import java.util.List;

public class WorkflowExecutorResult {
    public boolean requeue;
    public List<EventSubscription> subscriptions;

    public WorkflowExecutorResult() {
        this.subscriptions = new ArrayList<>();
    }
}
