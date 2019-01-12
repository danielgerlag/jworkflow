package net.jworkflow.providers.aws;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;

public interface DynamoDBProvisioner {

    void ensureTables() throws AwsServiceException, SdkClientException;
    
}
