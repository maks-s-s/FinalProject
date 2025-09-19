package com.epam.rd.autocode.spring.project.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

public class DefaultTokenCookieFactory implements Function<Authentication, Token> {

    private Duration tokenDuration = Duration.ofDays(1);

    @Override
    public Token apply(Authentication authentication) {
        var now = Instant.now();

        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.startsWith("ROLE_") ? auth : "ROLE_" + auth)
                .toList();

        return new Token(UUID.randomUUID(), authentication.getName(), authorities, now, now.plus(this.tokenDuration));
    }
}
