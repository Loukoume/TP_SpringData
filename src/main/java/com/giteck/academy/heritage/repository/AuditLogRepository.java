package com.giteck.academy.heritage.repository;

import com.giteck.academy.heritage.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}