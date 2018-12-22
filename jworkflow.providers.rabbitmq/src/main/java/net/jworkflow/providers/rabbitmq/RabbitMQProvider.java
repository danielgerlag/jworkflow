package net.jworkflow.providers.rabbitmq;

import com.google.inject.Provider;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQProvider implements Provider<RabbitMQQueueService> {

    private final ConnectionFactory connectionFactory;
    
    public RabbitMQProvider(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    
    @Override
    public RabbitMQQueueService get() {
        try {
            return new RabbitMQQueueService(connectionFactory);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
