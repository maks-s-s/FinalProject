package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "deactivated_token")
@NoArgsConstructor
@AllArgsConstructor
public class DeactivatedToken {
    @Id
    private UUID id;

    @Column(name = "deactivated_at", nullable = false)
    private Instant deactivatedAt;

    @Column(name = "keep_until", nullable = false)
    private Instant keepUntil;
}