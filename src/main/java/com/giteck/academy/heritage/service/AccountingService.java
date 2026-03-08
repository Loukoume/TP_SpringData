package com.giteck.academy.heritage.service;

import com.giteck.academy.heritage.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountingService {

    private final PaymentRepository paymentRepository;

    public AccountingService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // On utilise SERIALIZABLE pour garantir qu'aucun Phantom Read ne faussera
    // notre agrégation totale pendant l'exécution de cette méthode.
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void performDailyAccountingClosure() {
        // 1. Lecture de tous les paiements (Verrouille la table/les lignes selon le SGBD)
        BigDecimal totalAmount = paymentRepository.findAll().stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Traitement long (simulation)
        // ... génération du rapport comptable avec totalAmount ...

        System.out.println("Clôture terminée. Total : " + totalAmount);
    }
}