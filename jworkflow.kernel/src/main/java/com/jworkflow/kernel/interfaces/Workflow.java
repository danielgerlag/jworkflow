package com.jworkflow.kernel.interfaces;

import com.jworkflow.kernel.services.TypedWorkflowBuilder;

public interface Workflow<TData> {
    String getId();
    Class getDataType();
    int getVersion();
    void build(TypedWorkflowBuilder<TData> builder);
}
