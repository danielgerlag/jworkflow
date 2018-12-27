package net.jworkflow.providers.aws;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBFluentMapper<T> {
    
    public enum AttributeType { STRING, NUMBER, MAP, LIST, BOOL, BINARY }  
    
    private final Map<String, Function<T, ?>> getMaps;
    private final Map<String, BiConsumer<T, Object>> setMaps;
    private final Map<String, AttributeType> typeMaps;
    private final Class<T> dataClass;
    
    public DynamoDBFluentMapper(Class<T> dataClass) {
        getMaps = new HashMap<>();
        setMaps = new HashMap<>();
        typeMaps = new HashMap<>();
        this.dataClass = dataClass;
    }
    
    public DynamoDBFluentMapper<T> withField(String name, AttributeType type, Function<T, Object> getter, BiConsumer<T, Object> setter) {
        getMaps.put(name, getter);
        setMaps.put(name, setter);
        typeMaps.put(name, type);
        
        return this;
    }
    
    public DynamoDBFluentMapper<T> withString(String name, Function<T, String> getter, BiConsumer<T, String> setter) {
        getMaps.put(name, getter);
        setMaps.put(name, (obj, value) -> setter.accept(obj, String.valueOf(value)));
        //setMaps.put(name, setter);
        typeMaps.put(name, AttributeType.STRING);
        
        return this;
    }
    
    public DynamoDBFluentMapper<T> withLong(String name, Function<T, Long> getter, BiConsumer<T, Long> setter) {
        getMaps.put(name, getter);
        setMaps.put(name, (obj, value) -> setter.accept(obj, new Long(String.valueOf(value))));
        typeMaps.put(name, AttributeType.NUMBER);
        
        return this;
    }
    
    public DynamoDBFluentMapper<T> withInteger(String name, Function<T, Integer> getter, BiConsumer<T, Integer> setter) {
        getMaps.put(name, getter);
        setMaps.put(name, (obj, value) -> setter.accept(obj, new Integer(String.valueOf(value))));
        typeMaps.put(name, AttributeType.NUMBER);
        
        return this;
    }
    
    /*public DynamoDBFluentMapper<T> withList(String name, Function<T, Object> getter, BiConsumer<T, Object> setter) {
        getMaps.put(name, getter);
        setMaps.put(name, setter);
        typeMaps.put(name, AttributeType.LIST);
        
        return this;
    }*/
    
    public DynamoDBFluentMapper<T> withList(String name, Function<T, List<E>> getter, BiConsumer<T, List<E>> setter) {
        getMaps.put(name, getter);
        setMaps.put(name, setter);
        typeMaps.put(name, AttributeType.LIST);
        
        return this;
    }
    
    public DynamoDBFluentMapper<T> withMap(String name, Function<T, Object> getter, BiConsumer<T, Object> setter) {
        getMaps.put(name, getter);
        setMaps.put(name, setter);
        typeMaps.put(name, AttributeType.MAP);
        
        return this;
    }
    
    public DynamoDBFluentMapper<T> withBool(String name, Function<T, Boolean> getter, BiConsumer<T, Boolean> setter) {
        getMaps.put(name, getter);        
        setMaps.put(name, (obj, value) -> setter.accept(obj, Boolean.valueOf(String.valueOf(value))));
        typeMaps.put(name, AttributeType.BOOL);
        
        return this;
    }
    
    public T map(Map<String, AttributeValue> source) throws InstantiationException, IllegalAccessException {
        
        T result = dataClass.newInstance();
        
        setMaps.forEach((name, setter) -> {
            AttributeValue av = source.get(name);            
            
            switch (typeMaps.get(name)) {
                case STRING:
                    setter.accept(result, av.s());
                    break;
                case NUMBER:
                    setter.accept(result, av.n());
                    break;
                case BOOL:
                    setter.accept(result, av.bool());
                    break;
                case MAP:
                    setter.accept(result, av.m());
                    break;
                case LIST:
                    setter.accept(result, av.l());
                    break;
                case BINARY:
                    setter.accept(result, av.b());
                    break;
            }            
        });        
        
        return result;
    }
    
    public Map<String, AttributeValue> map(T source) {
        Map<String, AttributeValue> result = new HashMap<>();
        
        getMaps.forEach((name, getter) -> {
            
            Object value = getter.apply(source);
            
            AttributeValue.Builder builder = AttributeValue.builder();
            switch (typeMaps.get(name)) {
                case STRING:
                    builder = builder.s(String.valueOf(value));
                    break;
                case NUMBER:
                    builder = builder.n(String.valueOf(value));
                    break;
                case BOOL:
                    builder = builder.bool((Boolean)value);
                    break;
                case MAP:
                    builder = builder.m((Map<String, AttributeValue>)value);
                    break;
                case LIST:
                    builder = builder.l((Collection<AttributeValue>)value);
                    break;
                case BINARY:
                    builder = builder.b((SdkBytes)value);
                    break;
            }
            
            result.put(name, builder.build());
        });
        
        return result;
    }
    
}
