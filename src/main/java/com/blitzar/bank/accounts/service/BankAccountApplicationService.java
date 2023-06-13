package com.blitzar.bank.accounts.service;

import com.blitzar.bank.accounts.service.event.BankAccountApplicationEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

@Singleton
public class BankAccountApplicationService {

    @Inject
    private final BankAccountApplicationEventProducer eventProducer;
    private final Validator validator;

    public BankAccountApplicationService(BankAccountApplicationEventProducer eventProducer, Validator validator) {
        this.eventProducer = eventProducer;
        this.validator = validator;
    }

    public void registerApplication(BankAccountApplicationEvent bankAccountApplicationEvent){
        var eventValidations = validator.validate(bankAccountApplicationEvent);
        if(!eventValidations.isEmpty()){
            throw new ConstraintViolationException(eventValidations);
        }

        eventProducer.sendMessage(bankAccountApplicationEvent);
    }
}
