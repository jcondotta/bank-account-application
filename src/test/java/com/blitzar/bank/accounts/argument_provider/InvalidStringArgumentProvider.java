package com.blitzar.bank.accounts.argument_provider;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static groovy.json.StringEscapeUtils.escapeJava;

public class InvalidStringArgumentProvider implements ArgumentsProvider {

    private Named<String> emptyStringDisplayArgument = Named.of("\"\"", StringUtils.EMPTY);
    private Named<String> spacedStringDisplayArgument = Named.of("\" \"", StringUtils.SPACE);
    private Named<String> newlineDisplayArgument = Named.of("\"" + escapeJava(StringUtils.LF) + "\"", StringUtils.LF);
    private Named<String> horizontalTabDisplayArgument = Named.of("\"" + escapeJava("\t") + "\"", "\t");
    private Named<String> nullDisplayArgument = Named.of("null", null);

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(emptyStringDisplayArgument, StringUtils.EMPTY),
                Arguments.of(spacedStringDisplayArgument, StringUtils.SPACE),
                Arguments.of(newlineDisplayArgument, StringUtils.LF),
                Arguments.of(horizontalTabDisplayArgument, "\t"),
                Arguments.of(nullDisplayArgument, null)
        );
    }
}