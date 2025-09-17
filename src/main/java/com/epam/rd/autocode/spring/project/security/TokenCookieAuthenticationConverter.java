package com.epam.rd.autocode.spring.project.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class TokenCookieAuthenticationConverter implements AuthenticationConverter {

    private final Function<String, Token> tokenCookieStringDeserializer;

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("__Host-auth-token"))
                    .findFirst()
                    .map(cookie -> {
                        String value = cookie.getValue();
                        if (value == null || value.isEmpty()) {return null;}
                        Token token;
                        try {
                            token = this.tokenCookieStringDeserializer.apply(value);
                        } catch (Exception e) {return null;}
                        if (token == null) {return null;}
                        return new PreAuthenticatedAuthenticationToken(token, value);
                    }).orElse(null);
        }
        return null;
    }
}
