package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.repo.DeactivatedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {

    private final DeactivatedTokenRepository repository;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        repository.deleteByKeepUntilBefore(Instant.now());
    }
}
