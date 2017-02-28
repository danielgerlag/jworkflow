package com.jworkflow.kernel.interfaces;

import java.util.function.Function;

public interface StepPropertySupplier<TStep extends StepBody> extends Function<TStep, Object> {
    
}
