package com.jworkflow.providers.mongodb;

import com.jworkflow.kernel.models.ExecutionPointer;
import com.jworkflow.kernel.models.WorkflowStatus;
import java.util.Date;
import java.util.List;

public class PersistedWorkflow {
    
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
    
}
