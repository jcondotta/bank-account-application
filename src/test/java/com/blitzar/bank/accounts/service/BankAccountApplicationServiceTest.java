package com.blitzar.bank.accounts.service;

import com.blitzar.bank.accounts.argument_provider.InvalidStringArgumentProvider;
import com.blitzar.bank.accounts.service.event.AccountHolder;
import com.blitzar.bank.accounts.service.event.BankAccountApplicationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BankAccountApplicationServiceTest {

    private BankAccountApplicationService bankAccountApplicationService;
    private Validator validator;

    @Mock
    private BankAccountApplicationEventProducer eventProducer;

    @BeforeEach
    public void beforeEach(){
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        bankAccountApplicationService = new BankAccountApplicationService(eventProducer, validator);
    }

    @Test
    public void givenValidRequest_whenBankAccountApplication_thenSendMessage() throws JsonProcessingException {
        var accountHolderName = "Jefferson Condotta";
        var accountHolderDateOfBirth = LocalDate.of(1988, Month.JUNE, 20);

        var accountHolder = new AccountHolder(accountHolderName, accountHolderDateOfBirth);
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        bankAccountApplicationService.registerApplication(bankAccountApplicationEvent);

        verify(eventProducer).sendMessage(bankAccountApplicationEvent);
    }

    @Test
    public void givenEmptyAccountHolders_whenAddBankAccount_thenThrowException(){
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(List.of());

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankAccountApplicationService.registerApplication(bankAccountApplicationEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be empty"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders")
                ));

        verify(eventProducer, never()).sendMessage(bankAccountApplicationEvent);
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountHolderName_whenAddBankAccount_thenReturnBadRequest(String invalidAccountHolderName){
        var accountHolder = new AccountHolder(invalidAccountHolderName, LocalDate.of(1988, Month.JUNE, 20));
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankAccountApplicationService.registerApplication(bankAccountApplicationEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be blank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders[0].accountHolderName")
                ));

        verify(eventProducer, never()).sendMessage(bankAccountApplicationEvent);
    }

    @Test
    public void givenNullAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
        var accountHolder = new AccountHolder("Jefferson Condotta", null);
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankAccountApplicationService.registerApplication(bankAccountApplicationEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must not be null"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders[0].dateOfBirth")
                ));

        verify(eventProducer, never()).sendMessage(bankAccountApplicationEvent);
    }

    @Test
    public void givenFutureAccountHolderDateOfBirth_whenAddBankAccount_thenReturnBadRequest(){
        var accountHolder = new AccountHolder("Jefferson Condotta", LocalDate.now().plusDays(1));
        var bankAccountApplicationEvent = new BankAccountApplicationEvent(accountHolder);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankAccountApplicationService.registerApplication(bankAccountApplicationEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("must be a past date"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountHolders[0].dateOfBirth")
                ));

        verify(eventProducer, never()).sendMessage(bankAccountApplicationEvent);
    }
}