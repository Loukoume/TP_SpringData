package com.giteck.academy.heritage.service;

import com.giteck.academy.heritage.entity.Payment;
import com.giteck.academy.heritage.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentProcessorService {

    private final PaymentRepository paymentRepository;
    private final AuditService auditService; // Solution à la self-invocation : un autre bean

    public PaymentProcessorService(PaymentRepository paymentRepository, AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.auditService = auditService;
    }

    // Par défaut REQUIRED. On force le rollback même en cas d'exception vérifiée (Exception).
    @Transactional(rollbackFor = Exception.class)
    public void processPayment(Payment payment) throws Exception {

        // 1. Sauvegarde initiale du paiement
        paymentRepository.save(payment);

        try {
            // 2. Appel au système bancaire externe (qui peut lever une exception)
            callExternalBankApi(payment);

            // 3. Appel d'un autre Bean : REQUIRES_NEW sera respecté
            auditService.logPaymentAttempt(payment.getId(), "SUCCESS");

        } catch (Exception e) {
            // L'audit sera commité car il est en REQUIRES_NEW
            auditService.logPaymentAttempt(payment.getId(), "FAILED");

            // On relance l'exception pour déclencher le rollback de `processPayment`
            // Puisqu'on a mis rollbackFor = Exception.class, le paiement sauvegardé à l'étape 1 sera annulé ! [cite: 320, 321]
            throw e;
        }
    }

    private void callExternalBankApi(Payment payment) throws Exception {
        // Simulation d'une erreur réseau
        throw new Exception("API Bancaire injoignable");
    }
}