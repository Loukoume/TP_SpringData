package com.giteck.academy.heritage.controller;

import com.giteck.academy.heritage.dto.CardPaymentForm;
import com.giteck.academy.heritage.entity.CardPayment;
import com.giteck.academy.heritage.repository.PaymentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/payments")
public class PaymentUIController {

    private final PaymentRepository paymentRepository;

    public PaymentUIController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // 1. AFFICHER LE FORMULAIRE
    @GetMapping("/new")
    public String showPaymentForm(Model model) {
        // On place un objet vide dans le modèle pour que Thymeleaf puisse s'y lier
        model.addAttribute("paymentForm", new CardPaymentForm());
        return "payment-form"; // Retourne la vue payment-form.html
    }

    // 2. TRAITER LE FORMULAIRE AVEC @ModelAttribute
    @PostMapping("/new")
    public String processPayment(@ModelAttribute("paymentForm") CardPaymentForm form) {

        System.out.println("Données reçues du formulaire : " + form);

        // La conversion a été faite automatiquement !
        // Spring a pris la chaîne de caractères "150.00" tapée dans le HTML
        // et l'a convertie en objet BigDecimal grâce au Data Binding.

        // On convertit notre DTO en véritable Entité JPA pour la sauvegarder
        CardPayment cardPayment = new CardPayment();
        cardPayment.setAmount(form.getAmount());
        cardPayment.setCardLast4(form.getCardLast4());
        cardPayment.setNetwork(form.getNetwork());
        // (Optionnel : chercher le User en base via form.getUserId() et l'associer)

        paymentRepository.save(cardPayment);

        // Pattern POST-REDIRECT-GET : On redirige l'utilisateur pour éviter
        // qu'il ne soumette le formulaire deux fois s'il rafraîchit la page.
        return "redirect:/ui/payments/success";
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "payment-success";
    }
}