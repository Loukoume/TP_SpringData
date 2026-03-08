package com.giteck.academy.heritage.service;

import com.giteck.academy.heritage.entity.CardPayment;
import com.giteck.academy.heritage.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class PaymentRollbackService {

    private final PaymentRepository paymentRepository;

    public PaymentRollbackService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // --- SCÉNARIO 1 : Exception UNCHECKED (Bug) ---
    @Transactional
    public void testUncheckedException() {
        // 1. On sauvegarde un paiement en base
        CardPayment payment = new CardPayment();
        payment.setAmount(new BigDecimal("100.00"));
        paymentRepository.save(payment);

        System.out.println("Paiement 1 sauvegardé en mémoire/contexte JPA.");

        // 2. On provoque un bug volontaire (RuntimeException)
        // Spring va attraper cette erreur et faire un ROLLBACK automatique.
        throw new IllegalArgumentException("BOUM ! Erreur inattendue (Unchecked).");
    }

    // --- SCÉNARIO 2 : Exception CHECKED (Erreur prévue) ---
    @Transactional
    public void testCheckedExceptionDefaultBehavior() throws IOException {
        // 1. On sauvegarde un autre paiement
        CardPayment payment = new CardPayment();
        payment.setAmount(new BigDecimal("200.00"));
        paymentRepository.save(payment);

        System.out.println("Paiement 2 sauvegardé en mémoire/contexte JPA.");

        // 2. On provoque une erreur de fichier (Checked Exception)
        // DANGER : Par défaut, Spring NE FAIT PAS de rollback pour ça ! Le paiement sera persisté.
        throw new IOException("Fichier reçu introuvable (Checked).");
    }

    // --- SCÉNARIO 3 : La CORRECTION du Scénario 2 ---
    @Transactional(rollbackFor = IOException.class) // <-- LA SOLUTION
    public void testCheckedExceptionFixed() throws IOException {
        // 1. On sauvegarde un troisième paiement
        CardPayment payment = new CardPayment();
        payment.setAmount(new BigDecimal("300.00"));
        paymentRepository.save(payment);

        System.out.println("Paiement 3 sauvegardé en mémoire/contexte JPA.");

        // 2. On provoque la même erreur de fichier
        // Cette fois, grâce au paramètre rollbackFor, Spring annulera le paiement !
        throw new IOException("Fichier reçu introuvable (Checked) - Mais on annule !");
    }
}