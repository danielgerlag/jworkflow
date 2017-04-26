package com.jworkflow.kernel.services;
import com.jworkflow.kernel.services.abstractions.PersistenceServiceTest;
import org.junit.Test;
import com.jworkflow.kernel.interfaces.PersistenceService;

public class MemoryPersistenceServiceTest extends PersistenceServiceTest {

    @Override
    public PersistenceService createService() {
        return new MemoryPersistenceService();
    }
    
}
