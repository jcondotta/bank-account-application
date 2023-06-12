package com.blitzar.bank.accounts.factory;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import java.time.Clock;

@Factory
public class ClockFactory {

    @Bean
    public Clock systemUTCClock() {
        return Clock.systemUTC();
    }
}