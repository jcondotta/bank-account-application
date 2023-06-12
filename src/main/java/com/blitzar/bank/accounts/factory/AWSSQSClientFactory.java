package com.blitzar.bank.accounts.factory;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Factory
public class AWSSQSClientFactory {

    @Value("${aws.region}")
    private String region;

    @Singleton
    AmazonSQS sqsClient(AWSStaticCredentialsProvider awsStaticCredentialsProvider) {
        return AmazonSQSClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(awsStaticCredentialsProvider)
                .build();
    }
}