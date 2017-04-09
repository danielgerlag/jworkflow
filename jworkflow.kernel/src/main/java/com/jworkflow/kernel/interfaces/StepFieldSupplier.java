package com.jworkflow.kernel.interfaces;

import java.util.function.Function;

public interface StepFieldSupplier<TStep extends StepBody> extends Function<TStep, Object> {
    
}
