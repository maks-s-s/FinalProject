package com.epam.rd.autocode.spring.project.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

@Component
public class PublicUrlConfig {

    private RequestMatcher requestMatcher;

    public PublicUrlConfig() {
        this.requestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher("/login"),
                new AntPathRequestMatcher("/register"),
                new AntPathRequestMatcher("/error"),
                new AntPathRequestMatcher("/forgot-password"),
                new AntPathRequestMatcher("/reset-password"),
                new AntPathRequestMatcher("/oauth2/**"),
                new AntPathRequestMatcher("/login/oauth2/**")
        );
    }

    public RequestMatcher getRequestMatcher() {
        return requestMatcher;
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }
}

