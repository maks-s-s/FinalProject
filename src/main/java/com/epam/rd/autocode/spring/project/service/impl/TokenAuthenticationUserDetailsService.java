package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.exception.InvalidJwtToken;
import com.epam.rd.autocode.spring.project.repo.DeactivatedTokenRepository;
import com.epam.rd.autocode.spring.project.security.Token;
import com.epam.rd.autocode.spring.project.security.TokenUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TokenAuthenticationUserDetailsService
        implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final DeactivatedTokenRepository deactivatedTokenRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken)
            throws UsernameNotFoundException {
        if (authenticationToken.getPrincipal() instanceof Token token) {
            UUID tokenId = token.id();
            boolean notDeactivated = !deactivatedTokenRepository.existsById(tokenId);
            boolean notExpired = token.expiresAt().isAfter(Instant.now());

            if (token.authorities().contains("PASSWORD_RECOVERY")) {throw new InvalidJwtToken("Cannot authenticate with password recovery token");}

            boolean hasRole = token.authorities().stream().anyMatch(a -> a.startsWith("ROLE_"));
            if (!hasRole) {throw new InvalidJwtToken("User has no valid role");}

            return new TokenUser(
                    token.subject(),
                    "nopassword",
                    true,
                    true,
                    notDeactivated && notExpired,
                    true,
                    token.authorities().stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList(),
                    token
            );
        }

        throw new InvalidJwtToken("Principal must be of type Token");
    }
}
