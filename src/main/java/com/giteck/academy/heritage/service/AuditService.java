package com.giteck.academy.heritage.service;

import com.giteck.academy.heritage.entity.AuditLog;
import com.giteck.academy.heritage.repository.AuditLogRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final JdbcTemplate jdbc;
    private final AuditLogRepository auditLogRepository;

    public AuditService(JdbcTemplate jdbc, AuditLogRepository auditLogRepository) {
        this.jdbc = jdbc;
        this.auditLogRepository = auditLogRepository;
    }

    // REQUIRES_NEW : garantit que ce log sera commité même si l'appelant fail.
    // À réserver à des cas précis pour ne pas saturer le pool de connexions.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logPaymentAttempt(Long paymentId, String status) {
        jdbc.update("INSERT INTO audit_log (payment_id, status) VALUES (?, ?)", paymentId, status);
    }

    // REQUIRES_NEW : Je suspends la transaction en cours, je prends mon propre taxi,
    // je sauvegarde, et je valide ma propre transaction de façon indépendante.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logActionIndependently(String message) {
        auditLogRepository.save(new AuditLog(message));
        System.out.println("🚕 [TAXI INDÉPENDANT] Log sauvegardé : " + message);
    }
}