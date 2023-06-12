package com.blitzar.bank.accounts.web.controller;

import com.blitzar.bank.accounts.service.BankAccountApplicationService;
import com.blitzar.bank.accounts.service.event.BankAccountApplicationEvent;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.Validated;

@Validated
@Controller(BankAccountAPIConstants.BASE_PATH_API_V1_MAPPING)
public class BankAccountApplicationController {

    private final BankAccountApplicationService bankAccountApplicationService;

    public BankAccountApplicationController(BankAccountApplicationService bankAccountApplicationService) {
        this.bankAccountApplicationService = bankAccountApplicationService;
    }

    @Status(HttpStatus.ACCEPTED)
    @Post(value = "/application", consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> registerCardApplication(@Body BankAccountApplicationEvent bankAccountApplicationEvent){
        bankAccountApplicationService.request(bankAccountApplicationEvent);

        return HttpResponse.accepted().body("Your Bank account application has been accepted and will be processed soon");
    }
}
