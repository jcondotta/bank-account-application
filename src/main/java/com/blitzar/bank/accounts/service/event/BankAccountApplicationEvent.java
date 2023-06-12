package com.blitzar.bank.accounts.service.event;

import io.micronaut.core.annotation.Introspected;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Introspected
public class BankAccountApplicationEvent {

    @Valid
    @NotEmpty
    private List<AccountHolder> accountHolders;

    public BankAccountApplicationEvent(List<AccountHolder> accountHolders) {
        this.accountHolders = accountHolders;
    }

    public BankAccountApplicationEvent(AccountHolder... accountHolders) {
        this(List.of(accountHolders));
    }

    public List<AccountHolder> getAccountHolders() {
        return accountHolders;
    }

    public void setAccountHolders(List<AccountHolder> accountHolders) {
        this.accountHolders = accountHolders;
    }

    @Override
    public String toString() {
        return "BankAccountApplicationEvent{" +
                "accountHolders=" + accountHolders +
                '}';
    }
}

