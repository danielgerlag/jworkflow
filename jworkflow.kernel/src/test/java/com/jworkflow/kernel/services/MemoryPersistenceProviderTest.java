package com.jworkflow.kernel.services;
import com.jworkflow.kernel.interfaces.PersistenceProvider;
import com.jworkflow.kernel.services.abstractions.PersistenceProviderTest;
import org.junit.Test;

public class MemoryPersistenceProviderTest extends PersistenceProviderTest {

    @Override
    public PersistenceProvider createProvider() {
        return new MemoryPersistenceProvider();
    }
    
}
