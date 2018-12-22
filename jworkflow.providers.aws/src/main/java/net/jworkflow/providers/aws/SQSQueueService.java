package net.jworkflow.providers.aws;

import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.jworkflow.kernel.interfaces.QueueService;
import net.jworkflow.kernel.models.QueueType;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Singleton
public class SQSQueueService implements QueueService {

    private final SqsClient sqsClient;
    private final Logger logger;
    private final String queuePrefix = "jworkflow-";
    private final Map<QueueType, String> queueUrls;
    private final int waitTime = 2;
    
    public SQSQueueService(Region region) {
        this.logger = Logger.getLogger(SQSQueueService.class.getName());
        sqsClient = SqsClient.builder()
                .region(region)
                .build();

        queueUrls = new HashMap<>();
        queueUrls.put(QueueType.WORKFLOW, createQueue(QueueType.WORKFLOW));
        queueUrls.put(QueueType.EVENT, createQueue(QueueType.EVENT));
    }
        
    @Override
    public void queueForProcessing(QueueType type, String id) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrls.get(type))
                .messageBody(id)
                .build();
        
        sqsClient.sendMessage(request);
    }

    @Override
    public String dequeueForProcessing(QueueType type) {
        ReceiveMessageResponse response = sqsClient.receiveMessage(x -> x
                .maxNumberOfMessages(1)
                .waitTimeSeconds(waitTime)
                .queueUrl(queueUrls.get(type))
        );
        
        for (Message msg: response.messages()) {
            sqsClient.deleteMessage(x -> x
                .queueUrl(queueUrls.get(type))
                .receiptHandle(msg.receiptHandle())
            );
            
            return msg.body();
        }
        
        return null;
    }
    
    @Override
    public boolean isDequeueBlocking() {
        return true;
    }
    
    private String createQueue(QueueType type) {
        String queueName = queuePrefix + type.toString();
        sqsClient.createQueue(builder -> builder.queueName(queueName));
        
        GetQueueUrlResponse getQueueUrlResponse = sqsClient
                .getQueueUrl(builder -> builder.queueName(queueName));
        return getQueueUrlResponse.queueUrl();
    }    
}
