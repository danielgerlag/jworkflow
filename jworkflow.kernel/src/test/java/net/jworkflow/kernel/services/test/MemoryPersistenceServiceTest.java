package net.jworkflow.kernel.services.test;
import net.jworkflow.kernel.services.MemoryPersistenceService;
import net.jworkflow.kernel.services.abstractions.PersistenceServiceTest;
import org.junit.Test;
import net.jworkflow.kernel.interfaces.PersistenceService;

public class MemoryPersistenceServiceTest extends PersistenceServiceTest {

    @Override
    public PersistenceService createService() {
        return new MemoryPersistenceService();
    }
    
}
