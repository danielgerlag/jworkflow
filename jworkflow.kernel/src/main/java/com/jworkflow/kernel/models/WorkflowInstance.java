package com.jworkflow.kernel.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkflowInstance {
    
    private String id;
    private String workflowDefintionId;
    private int version;
    private String description;
    private Long nextExecution;
    private WorkflowStatus status;
    private Object data;
    private Date createTime;
    private Date completeTime;
    private List<ExecutionPointer> executionPointers;
    
    public WorkflowInstance() {
        this.executionPointers = new ArrayList<>();
        
    }
    

    public String getId() {
         return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    /**
     * @return the workflowDefintionId
     */
    public String getWorkflowDefintionId() {
        return workflowDefintionId;
    }

    /**
     * @param workflowDefintionId the workflowDefintionId to set
     */
    public void setWorkflowDefintionId(String workflowDefintionId) {
        this.workflowDefintionId = workflowDefintionId;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the nextExecution
     */
    public Long getNextExecution() {
        return nextExecution;
    }

    /**
     * @param nextExecution the nextExecution to set
     */
    public void setNextExecution(Long nextExecution) {
        this.nextExecution = nextExecution;
    }

    /**
     * @return the status
     */
    public WorkflowStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the completeTime
     */
    public Date getCompleteTime() {
        return completeTime;
    }

    /**
     * @param completeTime the completeTime to set
     */
    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    /**
     * @return the executionPointers
     */
    public List<ExecutionPointer> getExecutionPointers() {
        return executionPointers;
    }

    public void setExecutionPointers(List<ExecutionPointer> pointers) {
        this.executionPointers = pointers;
    }
    
}
