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
    
    private Injector injector;
    
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
    
    public void build() {
        injector = Guice.createInjector(this);        
    }
    
    public void usePersistence(Provider<? extends PersistenceService> persistenceProvider) {
        this.persistenceProvider = persistenceProvider;
    }
    
    public void useQueue(Provider<? extends QueueService> queueProvider) {
        this.queueProvider = queueProvider;
    }
    
    public void useDistibutedLock(Provider<? extends LockService> lockProvider) {
        this.lockProvider = lockProvider;
    }
    
    public WorkflowHost getHost() {
        if (injector != null)
            return injector.getInstance(WorkflowHost.class);        
        return null;
    }
    
    public PersistenceService getPersistenceProvider() {
        if (injector != null)
            return injector.getInstance(PersistenceService.class);        
        return null;
    }
}
