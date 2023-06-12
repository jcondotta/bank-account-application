package com.blitzar.bank.accounts.service.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Introspected
public class AccountHolder {

    @NotBlank
    private String accountHolderName;

    @Past
    @NotNull
    private LocalDate dateOfBirth;

    public AccountHolder(String accountHolderName, LocalDate dateOfBirth) {
        this.accountHolderName = accountHolderName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public String toString() {
        return "AccountHolder{" +
                "accountHolderName='" + accountHolderName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}