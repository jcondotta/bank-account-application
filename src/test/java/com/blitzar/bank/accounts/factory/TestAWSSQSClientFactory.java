package com.blitzar.bank.accounts.factory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Factory
public class TestAWSSQSClientFactory {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.sqs.endpoint}")
    private String endPoint;

    @Primary
    @Singleton
    AmazonSQS sqsClient(AWSStaticCredentialsProvider awsStaticCredentialsProvider) {
        AmazonSQS amazonSQS = AmazonSQSClientBuilder
                .standard()
                .withCredentials(awsStaticCredentialsProvider)
                .withEndpointConfiguration(new EndpointConfiguration(endPoint, region))
                .build();

        return amazonSQS;
    }
}