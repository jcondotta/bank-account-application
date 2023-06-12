package com.blitzar.bank.accounts.factory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Factory
public class AWSCredentialsFactory {

    @Value("${aws.access-key-id}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Singleton
    public AWSCredentials buildAwsCredentials(){
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Singleton
    public AWSStaticCredentialsProvider buildAWSStaticCredentialsProvider(AWSCredentials awsCredentials){
        return new AWSStaticCredentialsProvider(awsCredentials);
    }
}
