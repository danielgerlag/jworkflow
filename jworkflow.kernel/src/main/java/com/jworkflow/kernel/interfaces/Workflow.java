package com.jworkflow.kernel.interfaces;

public interface Workflow<TData> {
    String getId();
    int getVersion();
    void build(WorkflowBuilder builder);
}
