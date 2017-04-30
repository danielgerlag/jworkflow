package net.jworkflow.kernel.services;

import net.jworkflow.kernel.interfaces.WorkflowRegistry;
import net.jworkflow.kernel.interfaces.WorkflowExecutor;
import net.jworkflow.kernel.interfaces.LockService;
import net.jworkflow.kernel.interfaces.QueueService;
import net.jworkflow.kernel.interfaces.PersistenceService;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.util.Providers;

public class WorkflowModule extends AbstractModule {  
    
    public Provider<? extends PersistenceService> persistenceProvider;
    public Provider<? extends QueueService> queueProvider;
    public Provider<? extends LockService> lockProvider;
    
    public WorkflowModule() {
        persistenceProvider = Providers.of(new MemoryPersistenceService());
        queueProvider = Providers.of(new SingleNodeQueueService());
        lockProvider = Providers.of(new SingleNodeLockService());
    }        
    
    @Override 
    protected void configure() {        
      bind(WorkflowHost.class).to(WorkflowHostImpl.class);
      bind(WorkflowExecutor.class).to(WorkflowExecutorImpl.class);
      bind(WorkflowRegistry.class).to(WorkflowRegistryImpl.class);
      
      //
      bind(PersistenceService.class).toProvider(persistenceProvider);
      bind(LockService.class).to(SingleNodeLockService.class);
      bind(QueueService.class).to(SingleNodeQueueService.class);
      
    }    
    
    private static Injector injector;
    
    public static void setup() {
        AbstractModule module = new WorkflowModule();        
        injector = Guice.createInjector(module);        
    }
    
    public static void setup(Provider<? extends PersistenceService> persistenceProvider) {
        WorkflowModule module = new WorkflowModule();
        module.persistenceProvider = persistenceProvider;
        injector = Guice.createInjector(module);
    }
    
    public static void setup(Provider<? extends PersistenceService> persistenceProvider, Provider<? extends QueueService> queueProvider, Provider<? extends LockService> lockProvider) {
        WorkflowModule module = new WorkflowModule();
        module.persistenceProvider = persistenceProvider;
        module.queueProvider = queueProvider;
        module.lockProvider = lockProvider;
        injector = Guice.createInjector(module);
    }
    
    public static WorkflowHost getHost() {
        if (injector != null)
            return injector.getInstance(WorkflowHost.class);        
        return null;
    }
    
    public static PersistenceService getPersistenceProvider() {
        if (injector != null)
            return injector.getInstance(PersistenceService.class);        
        return null;
    }
}
