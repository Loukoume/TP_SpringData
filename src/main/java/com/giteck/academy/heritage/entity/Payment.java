package com.giteck.academy.heritage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // on changera ensuite
@DiscriminatorColumn(name = "pay_type")
@Data
public abstract class Payment {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal amount;
    private Instant createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private User user;
}
