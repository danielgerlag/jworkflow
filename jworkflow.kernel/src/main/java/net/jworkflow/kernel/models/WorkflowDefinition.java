package net.jworkflow.kernel.models;


import java.util.ArrayList;
import java.util.List;

public final class WorkflowDefinition {
    private String id;
    private int version;
    private String description;
    private List<WorkflowStep> steps;
    private Class dataType;

    public WorkflowDefinition() {
        setSteps(new ArrayList<>());
    }


    public String getId() {
        return id;        
    }

    public void setId(String id) {
        this.id = id;
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
    
    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }

    /**
     * @return the dataType
     */
    public Class getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(Class dataType) {
        this.dataType = dataType;
    }
    
    public WorkflowStep findStep(int findId) {
        for (WorkflowStep step: steps) {
            if (step.getId() == findId)
                return step;
        }
        return null;
    }
    
}
