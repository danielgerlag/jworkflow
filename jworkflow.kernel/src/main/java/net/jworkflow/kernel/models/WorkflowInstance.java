package net.jworkflow.kernel.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WorkflowInstance implements Serializable {
    
    private String id;
    private String workflowDefintionId;
    private int version;
    private String description;
    private Long nextExecution;
    private WorkflowStatus status;
    private Object data;
    private Date createTimeUtc;
    private Date completeTimeUtc;
    private ExecutionPointerCollection executionPointers;
    
    public WorkflowInstance() {
        this.executionPointers = new ExecutionPointerCollection();
        
    }
    

    public String getId() {
         return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getWorkflowDefintionId() {
        return workflowDefintionId;
    }

    public void setWorkflowDefintionId(String workflowDefintionId) {
        this.workflowDefintionId = workflowDefintionId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(Long nextExecution) {
        this.nextExecution = nextExecution;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Date getCreateTimeUtc() {
        return createTimeUtc;
    }

    public void setCreateTimeUtc(Date createTime) {
        this.createTimeUtc = createTime;
    }

    public Date getCompleteTimeUtc() {
        return completeTimeUtc;
    }

    public void setCompleteTimeUtc(Date completeTime) {
        this.completeTimeUtc = completeTime;
    }

    public ExecutionPointerCollection getExecutionPointers() {
        return executionPointers;
    }

    public void setExecutionPointers(List<ExecutionPointer> pointers) {
        this.executionPointers = new ExecutionPointerCollection(pointers);
    }
    
    public boolean isBranchComplete(String parentId) {
        return executionPointers
                .findByStackFrame(parentId)
                .stream()
                .allMatch(x -> x.endTimeUtc != null);
    }
}