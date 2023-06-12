package com.blitzar.bank.accounts.service;

import com.blitzar.bank.accounts.service.event.BankAccountApplicationEvent;
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.jms.sqs.configuration.SqsConfiguration;
import io.micronaut.messaging.annotation.MessageBody;

@JMSProducer(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
public interface BankAccountApplicationEventProducer {

    @Queue("${app.aws.sqs.bank-account-application-queue-name}")
    void sendMessage(@MessageBody BankAccountApplicationEvent bankAccountApplicationEvent);

}
