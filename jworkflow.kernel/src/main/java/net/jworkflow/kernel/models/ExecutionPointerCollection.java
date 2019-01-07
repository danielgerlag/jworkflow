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

public class ExecutionPointerCollection implements Iterable<ExecutionPointer>, Serializable {
    
    private final List<ExecutionPointer> data;
    private final Map<String, ExecutionPointer> map;
    
    public ExecutionPointerCollection() {
        data = new ArrayList<>();
        map = new HashMap<>();        
    }
    
    public ExecutionPointerCollection(Collection<ExecutionPointer> seed) {
        data = new ArrayList<>(seed);
        map = new HashMap<>();
        for (ExecutionPointer p: seed) {
            map.put(p.id, p);
        }
    }
    
    public void add(ExecutionPointer pointer) {
        data.add(pointer);
        map.put(pointer.id, pointer);        
    }
    
    public ExecutionPointer findById(String id) {
        return map.get(id);
    }
    
    public Optional<ExecutionPointer> findOne(Predicate<ExecutionPointer> filter) {
        return data.stream().filter(filter).findFirst();
    }
    
    public List<ExecutionPointer> findMany(Predicate<ExecutionPointer> filter) {
        return data.stream().filter(filter).collect(Collectors.toList());
    }
    
    public Stream<ExecutionPointer> streamMany(Predicate<ExecutionPointer> filter) {
        return data.stream().filter(filter);
    }
    
    public Stream<ExecutionPointer> stream() {
        return data.stream();
    }

    @Override
    public Iterator<ExecutionPointer> iterator() {
        return data.iterator();
    }
    
}
