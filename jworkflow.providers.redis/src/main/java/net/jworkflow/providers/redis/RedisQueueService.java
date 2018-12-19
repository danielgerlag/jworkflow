package net.jworkflow.providers.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.QueueService;
import net.jworkflow.kernel.models.QueueType;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisQueueService implements QueueService {

    private final RedissonClient client;
    private final Logger logger;
    private final String queuePrefix = "jworkflow-";
    private final Map<QueueType, Queue> queues;
    private final int waitTime = 5;
    
    public RedisQueueService(Config config, Logger logger) {
        this.logger = logger;
        client = Redisson.create(config);

        queues = new HashMap<>();
        queues.put(QueueType.WORKFLOW, createQueue(QueueType.WORKFLOW));
        queues.put(QueueType.EVENT, createQueue(QueueType.EVENT));
    }
    
    @Override
    public void queueForProcessing(QueueType type, String id) {
        queues.get(type).add(id);
    }

    @Override
    public String dequeueForProcessing(QueueType type) {
        return (String)queues.get(type).poll();
    }
    
    @Override
    public boolean isDequeueBlocking() {
        return false;
    }
    
    private Queue createQueue(QueueType type) {
        String queueName = queuePrefix + type.toString();
        return client.getQueue(queueName);
    }
}
