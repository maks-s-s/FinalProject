package com.epam.rd.autocode.spring.project.security;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public class PasswordRecoveryTokenCookieFactory implements Function<String, Token> {

    private final Duration tokenDuration = Duration.ofMinutes(10);

    @Override
    public Token apply(String email) {
        var now = Instant.now();
        return new Token(
                UUID.randomUUID(),
                email,
                List.of("PASSWORD_RECOVERY"),
                now,
                now.plus(tokenDuration)
        );
    }
}
