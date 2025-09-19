package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.model.DeactivatedToken;
import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.model.enums.UserRole;
import com.epam.rd.autocode.spring.project.repo.DeactivatedTokenRepository;
import com.epam.rd.autocode.spring.project.repo.UserRepository;
import com.epam.rd.autocode.spring.project.service.UserService;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public DefaultTokenCookieFactory defaultTokenCookieFactory() {
        return new DefaultTokenCookieFactory();
    }

    @Bean
    public PasswordRecoveryTokenCookieFactory passwordRecoveryTokenCookieFactory() {
        return new PasswordRecoveryTokenCookieFactory();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
            TokenCookieJweStringDeserializer deserializer,
            DeactivatedTokenRepository deactivatedTokenRepository,
            PublicUrlConfig publicUrlConfig) {

        return new TokenCookieAuthenticationConfigurer()
                .tokenCookieStringDeserializer(deserializer)
                .deactivatedTokenRepository(deactivatedTokenRepository)
                .requestMatcher(new NegatedRequestMatcher(publicUrlConfig.getRequestMatcher()));
    }

    @Bean
    public TokenCookieJweStringDeserializer tokenCookieJweStringDeserializer(
            @Value("${jwt.cookie-token-key}") String cookieTokenKey) throws Exception {
        return new TokenCookieJweStringDeserializer(
                new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))
        );
    }

    @Bean
    public TokenCookieJweStringSerializer tokenCookieJweStringSerializer(
            @Value("${jwt.cookie-token-key}") String cookieTokenKey
    ) throws ParseException, KeyLengthException {
        return new TokenCookieJweStringSerializer(new DirectEncrypter(
                OctetSequenceKey.parse(cookieTokenKey)
        ));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer,
            TokenCookieJweStringSerializer tokenCookieJweStringSerializer,
            DeactivatedTokenRepository deactivatedTokenRepository,
            PublicUrlConfig publicUrlConfig,
            UserService userService,
            DefaultTokenCookieFactory defaultTokenCookieFactory) throws Exception {



        var tokenCookieSessionAuthenticationStrategy = new TokenCookieSessionAuthenticationStrategy();
        tokenCookieSessionAuthenticationStrategy.setTokenStringSerializer(tokenCookieJweStringSerializer);

        http
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?status=error_invalid_credentials"))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                            String email = oauthUser.getAttribute("email");

                            User user = userService.findByEmail(email)
                                    .orElseGet(() -> userService.registerUser(
                                            oauthUser.getAttribute("name"),
                                            email,
                                            UUID.randomUUID().toString(),
                                            UserRole.CUSTOMER
                                    ));

                            TokenUser tokenUser = new TokenUser(
                                    user.getEmail(),
                                    user.getPassword(),
                                    List.of(new SimpleGrantedAuthority(user.getRole().toString())),
                                    null
                            );

                            var token = defaultTokenCookieFactory.apply(new UsernamePasswordAuthenticationToken(tokenUser, null, tokenUser.getAuthorities()));
                            var tokenString = tokenCookieJweStringSerializer.apply(token);

                            Cookie cookie = new Cookie("__Host-auth-token", tokenString);
                            cookie.setPath("/");
                            cookie.setDomain(null);
                            cookie.setSecure(true);
                            cookie.setHttpOnly(true);
                            cookie.setMaxAge((int) ChronoUnit.SECONDS.between(Instant.now(), token.expiresAt()));

                            response.addCookie(cookie);
                            response.sendRedirect("/home");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(new CookieClearingLogoutHandler("__Host-auth-token"))
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null &&
                                    authentication.getPrincipal() instanceof TokenUser user) {

                                UUID tokenId = user.getToken().id();
                                Instant expiresAt = user.getToken().expiresAt();

                                DeactivatedToken entity = new DeactivatedToken(
                                        tokenId,
                                        Instant.now(),
                                        expiresAt
                                );
                                deactivatedTokenRepository.save(entity);
                            }
                        })
                        .logoutSuccessUrl("/login?status=info_logout_success"))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(publicUrlConfig.getRequestMatcher()).permitAll()
                                .anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .sessionAuthenticationStrategy(tokenCookieSessionAuthenticationStrategy))
                .csrf(csrf -> csrf.csrfTokenRepository(new CookieCsrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .sessionAuthenticationStrategy((authentication, request, response) -> {
                        }));

        tokenCookieAuthenticationConfigurer.configure(http);

        return http.build();
    }
}
