package com.giteck.academy.heritage.service;

import com.giteck.academy.heritage.entity.CardPayment;
import com.giteck.academy.heritage.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AdvancedPaymentService {

    private final PaymentRepository paymentRepository;
    private final AuditService auditService; // On injecte le taxi !

    public AdvancedPaymentService(PaymentRepository paymentRepository, AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.auditService = auditService;
    }

    // --- SCÉNARIO 1 : PROPAGATION (REQUIRED vs REQUIRES_NEW) ---
    // Propagation par défaut : REQUIRED (Le covoiturage solidaire)
    @Transactional(propagation = Propagation.REQUIRED)
    public void processPaymentWithFailure() {
        System.out.println("🚗 [COVOITURAGE] Début de la transaction principale...");

        // 1. On sauvegarde un paiement (dans la transaction principale)
        CardPayment payment = new CardPayment();
        payment.setAmount(new BigDecimal("500.00"));
        paymentRepository.save(payment);

        // 2. On appelle notre AuditService (qui ouvre SA propre transaction grâce à REQUIRES_NEW)
        auditService.logActionIndependently("Tentative de paiement de 500.00");

        // 3. Catastrophe ! Une erreur survient dans la transaction principale
        System.out.println("💥 [CRASH] Une erreur inattendue survient !");
        throw new RuntimeException("Erreur réseau bancaire !");
        // -> Le paiement de 500.00 sera annulé (Rollback du covoiturage).
        // -> MAIS le log sera conservé en base (Commit du taxi indépendant) !
    }

    // --- SCÉNARIO 2 : ISOLATION (SERIALIZABLE) ---
    // Isolation : SERIALIZABLE (Le coffre-fort)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void calculateTotalAndLock() throws InterruptedException {
        System.out.println("🔒 [COFFRE-FORT] Comptage des paiements initié...");

        long count1 = paymentRepository.count();
        System.out.println("🔒 [COFFRE-FORT] Premier comptage : " + count1 + " paiements.");

        // On simule un traitement long de 5 secondes.
        // Puisqu'on est en SERIALIZABLE, aucune autre transaction ne pourra insérer
        // de paiement dans la base pendant ces 5 secondes !
        Thread.sleep(5000);

        long count2 = paymentRepository.count();
        System.out.println("🔒 [COFFRE-FORT] Second comptage : " + count2 + " paiements.");
        System.out.println("🔒 [COFFRE-FORT] Le résultat est garanti identique (pas de Phantom Read).");
    }
}