package net.jworkflow.kernel.services;

import net.jworkflow.kernel.interfaces.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;
import java.time.Clock;
import net.jworkflow.kernel.services.errorhandlers.*;

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
      bind(WorkflowHost.class).to(DefaultWorkflowHost.class);
      bind(WorkflowExecutor.class).to(DefaultWorkflowExecutor.class);
      bind(WorkflowRegistry.class).to(DefaultWorkflowRegistry.class);
      bind(ExecutionPointerFactory.class).to(DefaultExecutionPointerFactory.class);
      bind(ExecutionResultProcessor.class).to(DefaultExecutionResultProcessor.class);
      bind(Clock.class).toInstance(Clock.systemUTC());
      
      Multibinder<StepErrorHandler> errorHandlerBinder = Multibinder.newSetBinder(binder(), StepErrorHandler.class);
      errorHandlerBinder.addBinding().to(RetryHandler.class);
      errorHandlerBinder.addBinding().to(CompensateHandler.class);
      errorHandlerBinder.addBinding().to(SuspendHandler.class);
      errorHandlerBinder.addBinding().to(TerminateHandler.class);
                  
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
