package com.blitzar.bank.accounts.web.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.PurgeQueueRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.blitzar.bank.accounts.AWSSQSTestContainer;
import com.blitzar.bank.accounts.argument_provider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.service.event.AccountHolder;
import com.blitzar.bank.accounts.service.event.BankAccountApplicationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class BankAccountApplicationControllerTest implements AWSSQSTestContainer {

    @Inject
    private AmazonSQS sqsClient;

    @Value("${app.aws.sqs.bank-account-application-queue-name}")
    private String bankAccountApplicationQueueName;

    private String bankAccountApplicationQueueURL;

    @Inject
    private ObjectMapper objectMapper;

    private RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(BankAccountAPIConstants.BANK_ACCOUNT_APPLICATION_V1_MAPPING);

        this.bankAccountApplicationQueueURL = sqsClient.createQueue(new CreateQueueRequest()
                        .withQueueName(bankAccountApplicationQueueName))
                .getQueueUrl();
    }

    @AfterEach
    public void afterEach() {
        sqsClient.purgeQueue(new PurgeQueueRequest(bankAccountApplicationQueueURL));
    }

    @Test
    public void givenValidRequest_whenBankAccountApplication_thenReturnAccepted() throws JsonProcessingException {
        var accountHolderName = "Jefferson Condotta";
        var accountHolderDateOfBirth = LocalDate.of(1988, Month.JUNE, 20);

        var accountHolder = new AccountHolder(accountHolderName, accountHolderDateOfBirth);
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        given()
            .spec(requestSpecification)
            .body(bankAccountApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.ACCEPTED.getCode());

        List<Message> messages = sqsClient.receiveMessage(new ReceiveMessageRequest()
                        .withQueueUrl(bankAccountApplicationQueueURL)
                        .withMaxNumberOfMessages(2))
                .getMessages();
        assertThat(messages).hasSize(1);

        for(Message message : messages) {
            var applicationEvent = objectMapper.readValue(message.getBody(), BankAccountApplicationEvent.class);

            Assertions.assertAll(
                    () -> assertThat(applicationEvent.getAccountHolders()).hasSize(1),
                    () -> assertThat(applicationEvent.getAccountHolders().get(0).getAccountHolderName()).isEqualTo(accountHolder.getAccountHolderName()),
                    () -> assertThat(applicationEvent.getAccountHolders().get(0).getDateOfBirth()).isEqualTo(accountHolder.getDateOfBirth())
            );
        }
    }

    @Test
    public void givenEmptyAccountHolders_whenAddBankAccount_thenReturnBadRequest(){
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(List.of());

        given()
            .spec(requestSpecification)
        .body(bankAccountApplicationEvent)
            .when()
        .post()
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.getCode())
                .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
                .rootPath("_embedded")
                    .body("errors", hasSize(1))
                    .body("errors[0].message", equalTo("accountHolders: must not be empty"));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderName){
        var accountHolder = new AccountHolder(invalidAccountHolderName, LocalDate.of(1988, Month.JUNE, 20));
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        given()
            .spec(requestSpecification)
            .body(bankAccountApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("accountHolders.accountHolderName[0]: must not be blank"));
    }

    @Test
    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
        var accountHolder = new AccountHolder("Jefferson Condotta", null);
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        given()
            .spec(requestSpecification)
            .body(bankAccountApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("accountHolders.dateOfBirth[0]: must not be null"));
    }

    @Test
    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
        var accountHolder = new AccountHolder("Jefferson Condotta", LocalDate.now().plusDays(1));
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        given()
            .spec(requestSpecification)
            .body(bankAccountApplicationEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .body("message", equalTo(HttpStatus.BAD_REQUEST.getReason()))
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo("accountHolders.dateOfBirth[0]: must be a past date"));
    }
}