package com.giteck.academy.heritage.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("MOMO")
@Data
public class MobileMoneyPayment extends Payment {
    private String operator; // MTN, MOOV
    private String phoneNumber;
}

