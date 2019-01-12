package net.jworkflow.sample03;

import com.rabbitmq.client.ConnectionFactory;
import net.jworkflow.kernel.interfaces.WorkflowHost;
import net.jworkflow.WorkflowModule;
import net.jworkflow.providers.mongodb.MongoPersistenceService;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jworkflow.providers.aws.DynamoDBLockProvider;
import net.jworkflow.providers.aws.DynamoDBPersistenceProvider;
import net.jworkflow.providers.aws.SQSProvider;
import net.jworkflow.providers.rabbitmq.RabbitMQProvider;
import net.jworkflow.providers.redis.RedisLockServiceProvider;
import net.jworkflow.providers.redis.RedisQueueServiceProvider;
import org.redisson.config.Config;
import software.amazon.awssdk.regions.Region;

public class Main {
    public static void main(String[] args) throws Exception {        
        
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.SEVERE);
        
        WorkflowModule module = new WorkflowModule();
        //module.usePersistence(MongoPersistenceService.configure("mongodb://localhost:27017/jworkflow"));        
        
        //Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        //module.useDistibutedLock(new RedisLockServiceProvider(config));
        //module.useQueue(new RedisQueueServiceProvider(config));
        
        //module.useQueue(new SQSProvider(Region.US_WEST_1));
        //module.useDistibutedLock(new DynamoDBLockProvider(Region.US_WEST_1, "jworkflowLocks"));
        module.usePersistence(new DynamoDBPersistenceProvider(Region.US_WEST_1, "test4"));
        
        
        module.build();
        WorkflowHost host = module.getHost();
        
        host.registerWorkflow(EventsWorkflow.class);
        
        host.start();
        MyData data = new MyData();        
        
        String id = host.startWorkflow("events-workflow", 1, data);
        System.out.println("started workflow " + id);        
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter value to publish");
        String inputData = scanner.nextLine();
        
        host.publishEvent("myEvent", "1", inputData, new Date());
        
        scanner.nextLine();
        System.out.println("shutting down...");
        host.stop();                        
        
    }
}