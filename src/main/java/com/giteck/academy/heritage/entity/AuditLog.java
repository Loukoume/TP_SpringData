package com.giteck.academy.heritage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.Instant;

@Entity(name = "auditlog")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private Instant createdAt = Instant.now();

    public AuditLog() {}

    public AuditLog(String message) {
        this.message = message;
    }
}