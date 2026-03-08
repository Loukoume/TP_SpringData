package com.giteck.academy.heritage.runner;

import com.giteck.academy.heritage.entity.CardPayment;
import com.giteck.academy.heritage.repository.AuditLogRepository;
import com.giteck.academy.heritage.repository.PaymentRepository;
import com.giteck.academy.heritage.service.AdvancedPaymentService;
import com.giteck.academy.heritage.service.PaymentRollbackService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionTestRunner implements CommandLineRunner {

    private final PaymentRollbackService rollbackService;
    private final AdvancedPaymentService advancedPaymentService;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditRepository;

    // Injection des dépendances via le constructeur
    public TransactionTestRunner(PaymentRollbackService rollbackService,
                                 AdvancedPaymentService advancedPaymentService,
                                 PaymentRepository paymentRepository,
                                 AuditLogRepository auditRepository) {
        this.rollbackService = rollbackService;
        this.advancedPaymentService = advancedPaymentService;
        this.paymentRepository = paymentRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=======================================================");
        System.out.println(" DÉMARRAGE DES TESTS TRANSACTIONNELS SPRING ");
        System.out.println("=======================================================\n");

        // -----------------------------------------------------------------
        // MODULE 1 : TEST DES ROLLBACKS (CHECKED vs UNCHECKED)
        // -----------------------------------------------------------------
        System.out.println("--- PARTIE 1 : ROLLBACK (CHECKED vs UNCHECKED) ---");

        try {
            rollbackService.testUncheckedException();
        } catch (Exception e) {
            System.out.println("Résultat Unchecked : " + e.getMessage());
        }

        try {
            rollbackService.testCheckedExceptionDefaultBehavior();
        } catch (Exception e) {
            System.out.println("Résultat Checked (Défaut) : " + e.getMessage());
        }

        try {
            rollbackService.testCheckedExceptionFixed();
        } catch (Exception e) {
            System.out.println("Résultat Checked (Corrigé) : " + e.getMessage());
        }

        System.out.println("-> Bilan Partie 1 (Base de données) :");
        System.out.println("Nombre de paiements enregistrés : " + paymentRepository.count() + " (Attendu: 1)");

        // -----------------------------------------------------------------
        // MODULE 2 : TEST PROPAGATION (REQUIRES_NEW)
        // -----------------------------------------------------------------
        System.out.println("\n--- PARTIE 2 : PROPAGATION (REQUIRES_NEW vs REQUIRED) ---");

        try {
            advancedPaymentService.processPaymentWithFailure();
        } catch (Exception e) {
            System.out.println("Résultat Propagation (Crash attendu) : " + e.getMessage());
        }

        System.out.println("-> Bilan Partie 2 :");
        System.out.println("Nombre total de paiements : " + paymentRepository.count() + " (Attendu: 1, car le 500 a subi un Rollback)");
        System.out.println("Nombre de logs d'audit : " + auditRepository.count() + " (Attendu: 1, car le Taxi REQUIRES_NEW a commité !)");

        // -----------------------------------------------------------------
        // MODULE 3 : TEST ISOLATION (SERIALIZABLE)
        // -----------------------------------------------------------------
        System.out.println("\n--- PARTIE 3 : ISOLATION (SERIALIZABLE) ---");

        // On lance le service qui verrouille la table dans un thread séparé
        Thread lockerThread = new Thread(() -> {
            try {
                advancedPaymentService.calculateTotalAndLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        lockerThread.start();

        // On laisse une petite seconde au thread pour démarrer et poser le verrou
        Thread.sleep(1000);

        System.out.println(" [PIRATE] J'essaie d'insérer un paiement furtif de 999.99 pendant que le coffre est verrouillé !");
        CardPayment piratePayment = new CardPayment();
        piratePayment.setAmount(new BigDecimal("999.99"));

        long startTime = System.currentTimeMillis();
        // Le thread principal (le pirate) va se retrouver BLOQUÉ ici par la base de données
        paymentRepository.save(piratePayment);
        long endTime = System.currentTimeMillis();

        System.out.println(" [PIRATE] Ouf, j'ai enfin pu insérer le paiement après avoir attendu " + (endTime - startTime) + " ms !");

        System.out.println("\n=======================================================");
        System.out.println("FIN DE TOUS LES TESTS TRANSACTIONNELS");
        System.out.println("=======================================================\n");
    }
}