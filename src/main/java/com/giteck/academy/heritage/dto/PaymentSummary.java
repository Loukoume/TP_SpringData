package com.giteck.academy.heritage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
public class PaymentSummary {
    Long id;
    BigDecimal amount;

}
