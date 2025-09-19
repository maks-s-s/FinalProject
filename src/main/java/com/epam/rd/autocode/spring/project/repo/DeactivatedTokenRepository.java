package com.epam.rd.autocode.spring.project.repo;

import com.epam.rd.autocode.spring.project.model.DeactivatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeactivatedTokenRepository extends JpaRepository<DeactivatedToken, UUID> {
    void deleteByKeepUntilBefore(Instant now);
}
