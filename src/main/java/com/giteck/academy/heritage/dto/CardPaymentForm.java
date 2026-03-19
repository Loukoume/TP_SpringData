package com.giteck.academy.heritage.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data // Génère les getters, setters, toString, etc.
public class CardPaymentForm {
    // Les noms de ces attributs DOIVENT correspondre aux attributs "name" du formulaire HTML
    private BigDecimal amount;
    private String cardLast4;
    private String network; // "VISA" ou "MASTERCARD"
    private Long userId;
}