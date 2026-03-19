package com.giteck.academy.heritage.controller;

import com.giteck.academy.heritage.entity.CardPayment;
import com.giteck.academy.heritage.entity.Payment;
import com.giteck.academy.heritage.service.PaymentProcessorService;
import com.giteck.academy.heritage.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessorService paymentProcessorService;

    public PaymentController(PaymentRepository paymentRepository, PaymentProcessorService paymentProcessorService) {
        this.paymentRepository = paymentRepository;
        this.paymentProcessorService = paymentProcessorService;
    }

    // 1. Retourne un objet Java (List<Payment>) -> Sérialisé en JSON automatiquement (Status 200)
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // 2. Utilisation de ResponseEntity pour contrôler le statut (201 Created)
    @PostMapping("/card")
    public ResponseEntity<String> createCardPayment(@RequestBody CardPayment payment) {
        try {
            paymentProcessorService.processPayment(payment);
            // On retourne un 201 avec un message de succès
            return ResponseEntity.status(HttpStatus.CREATED).body("Paiement par carte enregistré avec succès.");
        } catch (Exception e) {
            // En cas d'erreur de l'API bancaire (gérée dans ton service), on retourne un 400
            return ResponseEntity.badRequest().body("Erreur lors du traitement : " + e.getMessage());
        }
    }

    // 3. Utilisation de ResponseEntity pour le cas "No Content" (204)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Statut 204
        }
        return ResponseEntity.notFound().build(); // Statut 404
    }
}