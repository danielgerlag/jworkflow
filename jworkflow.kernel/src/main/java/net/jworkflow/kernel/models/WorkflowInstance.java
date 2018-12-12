package net.jworkflow.kernel.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

public class WorkflowInstance {
    
    private String id;
    private String workflowDefintionId;
    private int version;
    private String description;
    private Long nextExecution;
    private WorkflowStatus status;
    private Object data;
    private Date createTimeUtc;
    private Date completeTimeUtc;
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
    public Date getCreateTimeUtc() {
        return createTimeUtc;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTimeUtc(Date createTime) {
        this.createTimeUtc = createTime;
    }

    /**
     * @return the completeTime
     */
    public Date getCompleteTimeUtc() {
        return completeTimeUtc;
    }

    /**
     * @param completeTime the completeTime to set
     */
    public void setCompleteTimeUtc(Date completeTime) {
        this.completeTimeUtc = completeTime;
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
    
    public boolean isBranchComplete(String rootId) {
        
        Optional<ExecutionPointer> root = executionPointers.stream()
                .filter(x -> x.id.equals(rootId))
                .findFirst();
        
        if (root.get().endTimeUtc == null)
            return false;

        Collection<ExecutionPointer> seed = executionPointers.stream()
                .filter(x -> rootId.equals(x.predecessorId))
                .collect(Collectors.toList());        
        
        Queue<ExecutionPointer> queue = new LinkedList<>(seed);

        while (!queue.isEmpty()) {
            ExecutionPointer item = queue.remove();
            if (item.endTimeUtc == null) {
                return false;
            }

            executionPointers.stream()
                .filter(x -> item.id.equals(x.predecessorId))
                .forEach(child -> queue.add(child));
        }

        return true;        
    }
    
    public ExecutionPointer findExecutionPointer(String pointerId) {
        for (ExecutionPointer pointer: executionPointers) {
            if (pointer.id.equals(pointerId))
                return pointer;
        }
        return null;
    }
}