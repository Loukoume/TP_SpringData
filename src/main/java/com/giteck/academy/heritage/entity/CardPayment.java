package com.giteck.academy.heritage.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("CARD")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPayment extends Payment {
    private String cardLast4;
    private String network; // VISA, MASTERCARD
}

