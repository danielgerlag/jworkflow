package net.jworkflow.kernel.interfaces;

public interface Workflow<TData> {
    String getId();
    Class getDataType();
    int getVersion();
    void build(WorkflowBuilder<TData> builder);
}
