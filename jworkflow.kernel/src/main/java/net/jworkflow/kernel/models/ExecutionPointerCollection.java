package net.jworkflow.kernel.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExecutionPointerCollection implements Iterable<ExecutionPointer>, Serializable {
    
    private final Map<String, ExecutionPointer> data;
    private final Map<String, Collection<ExecutionPointer>> stackMap;
    
    public ExecutionPointerCollection() {
        data = new HashMap<>();
        stackMap = new HashMap<>();
    }
    
    public ExecutionPointerCollection(Collection<ExecutionPointer> seed) {
        stackMap = new HashMap<>(seed.size());
        data = new HashMap<>(seed.size());
        seed.stream().forEach((p) -> {
            add(p);
        });
    }
    
    public void add(ExecutionPointer pointer) {        
        data.put(pointer.id, pointer);        
        pointer.callStack.forEach(frame -> {
            if (!stackMap.containsKey(frame)) {
                stackMap.put(frame, new ArrayList<>());
            }
            stackMap.get(frame).add(pointer);
        });        
    }
    
    public ExecutionPointer findById(String id) {
        return data.get(id);
    }
    
    public Optional<ExecutionPointer> findOne(Predicate<ExecutionPointer> filter) {
        return data.values().stream().filter(filter).findFirst();
    }
    
    public List<ExecutionPointer> findMany(Predicate<ExecutionPointer> filter) {
        return data.values().stream().filter(filter).collect(Collectors.toList());
    }
    
    public Stream<ExecutionPointer> streamMany(Predicate<ExecutionPointer> filter) {
        return data.values().stream().filter(filter);
    }
    
    public Stream<ExecutionPointer> stream() {
        return data.values().stream();
    }

    @Override
    public Iterator<ExecutionPointer> iterator() {
        return data.values().iterator();
    }
    
    public Collection<ExecutionPointer> findByStackFrame(String frameId) {
        if (!stackMap.containsKey(frameId))
            return new ArrayList<>();
        
        return stackMap.get(frameId);
    }
    
}
