package com.blitzar.bank.accounts.factory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Factory
public class TestClockFactory {

    @Primary
    @Singleton
    public Clock testClock() {
        return Clock.fixed(
                Instant.parse("2022-06-24T12:45:01Z"),
                ZoneOffset.UTC);
    }
}
