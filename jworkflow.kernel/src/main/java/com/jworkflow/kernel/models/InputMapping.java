package com.jworkflow.kernel.models;

import com.jworkflow.kernel.interfaces.DataFieldSupplier;
import com.jworkflow.kernel.interfaces.StepFieldConsumer;

public class InputMapping {
    
    private StepFieldConsumer stepFieldConsumer;
    private DataFieldSupplier dataFieldSupplier;

    public StepFieldConsumer getStepFieldConsumer() {
        return stepFieldConsumer;
    }

    public void setStepFieldConsumer(StepFieldConsumer consumer) {
        this.stepFieldConsumer = consumer;
    }

    public DataFieldSupplier getDataFieldSupplier() {
        return dataFieldSupplier;
    }

    public void setDataFieldSupplier(DataFieldSupplier supplier) {
        this.dataFieldSupplier = supplier;
    }    
}
