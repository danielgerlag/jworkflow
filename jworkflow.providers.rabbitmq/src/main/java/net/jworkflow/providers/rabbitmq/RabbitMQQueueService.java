package net.jworkflow.providers.rabbitmq;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.GetResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.QueueService;
import net.jworkflow.kernel.models.QueueType;

public class RabbitMQQueueService implements QueueService {

    private final Connection connection;
    private final Logger logger;
    private final String queuePrefix = "jworkflow-";
    private final Map<QueueType, String> queueNames;
    private final int waitTime = 5;
    
    public RabbitMQQueueService(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
        this.logger = Logger.getLogger(getClass().getName());        
        connection = connectionFactory.newConnection();                
        queueNames = new HashMap<>();
        queueNames.put(QueueType.WORKFLOW, createQueue(QueueType.WORKFLOW));
        queueNames.put(QueueType.EVENT, createQueue(QueueType.EVENT));
    }
        
    @Override
    public void queueForProcessing(QueueType type, String id) {
        try (Channel channel = connection.createChannel()) {                        
            channel.queueDeclare(queueNames.get(type), true, false, false, null);
            channel.basicPublish("", queueNames.get(type), null, id.getBytes());
        } 
        catch (Exception ex) {
           logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String dequeueForProcessing(QueueType type) {
        try (Channel channel = connection.createChannel()) {                        
            channel.basicQos(1);
            channel.queueDeclare(queueNames.get(type), true, false, false, null);
            
            GetResponse msg = channel.basicGet(queueNames.get(type), true);
            if (msg == null)
                return null;
            
            return new String(msg.getBody(), "UTF-8");            
        } 
        catch (Exception ex) {
           logger.log(Level.SEVERE, null, ex);
           return null;
        }
    }
    
    @Override
    public boolean isDequeueBlocking() {
        return false;
    }
    
    private String createQueue(QueueType type) throws IOException {
        String queueName = queuePrefix + type.toString();
        return queueName;
    }    
}
