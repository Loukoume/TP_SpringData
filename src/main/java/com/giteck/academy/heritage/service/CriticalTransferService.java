package com.giteck.academy.heritage.service;

import com.giteck.academy.heritage.entity.CardPayment;
import com.giteck.academy.heritage.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CriticalTransferService {

    private final PaymentRepository paymentRepository;

    public CriticalTransferService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Traitement d'un paiement critique.
     * Cette méthode combine les 3 paramètres pour une sécurité maximale.
     */
    @Transactional(
            // 1. PROPAGATION (Le Taxi Indépendant)
            // Quoi qu'il arrive dans la méthode appelante, je veux ma propre transaction.
            // Si l'appelant fait un rollback, MON transfert reste validé (ou annulé) selon MES propres règles.
            propagation = Propagation.REQUIRES_NEW,

            // 2. ISOLATION (Le Coffre-fort)
            // Pendant que je vérifie les montants ou que j'insère ce transfert,
            // je verrouille l'accès pour éviter que quelqu'un d'autre ne modifie
            // les données en même temps (Phantom Read, etc.).
            isolation = Isolation.SERIALIZABLE,

            // 3. ROLLBACK (La Ceinture de sécurité)
            // Par défaut, Spring ne fait de rollback que sur les RuntimeException.
            // Ici, j'exige un rollback MÊME si une exception vérifiée (Exception) survient.
            rollbackFor = Exception.class
    )
    public void processCriticalBankTransfer(BigDecimal amount) throws Exception {

        System.out.println("🛡️ [TAXI BLINDÉ] Début de la transaction critique...");

        try {
            // 1. Sauvegarde du paiement
            CardPayment payment = new CardPayment();
            payment.setAmount(amount);
            paymentRepository.save(payment);

            // 2. Simulation d'un appel à la Banque Centrale qui prend du temps
            // Grâce à SERIALIZABLE, personne ne peut venir fausser nos calculs pendant ce temps.
            Thread.sleep(2000);

            // 3. Simulation d'une erreur réseau inattendue (Exception vérifiée)
            boolean networkError = true;
            if (networkError) {
                // Cette exception checked déclenchera un ROLLBACK grâce à 'rollbackFor = Exception.class'
                // Sans ce paramètre, le paiement de l'étape 1 aurait été commité à tort !
                throw new Exception("Le serveur de la Banque Centrale ne répond pas !");
            }

            System.out.println("🛡️ [TAXI BLINDÉ] Transfert réussi avec succès.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processus interrompu", e);
        }
    }
}