package net.jworkflow.kernel.interfaces;

import net.jworkflow.kernel.services.TypedWorkflowBuilder;

public interface Workflow<TData> {
    String getId();
    Class getDataType();
    int getVersion();
    void build(TypedWorkflowBuilder<TData> builder);
}
