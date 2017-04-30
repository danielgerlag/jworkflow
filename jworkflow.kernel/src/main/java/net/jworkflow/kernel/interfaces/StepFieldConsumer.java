package net.jworkflow.kernel.interfaces;

import java.util.function.BiConsumer;

public interface StepFieldConsumer<TStep extends StepBody, TData> extends BiConsumer<TStep, TData> {
    
}