package com.giteck.academy.heritage.repository;

import com.giteck.academy.heritage.dto.PaymentSummary;
import com.giteck.academy.heritage.entity.CardPayment;
import com.giteck.academy.heritage.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT new com.giteck.academy.heritage.dto.PaymentSummary(p.id, p.amount)
        FROM Payment p
    """)
    List<PaymentSummary> findAllPaymentSummaries();

    @Query("""
     select new com.giteck.academy.heritage.entity.CardPayment(p.id,p.class) from Payment  p
""")
    List<CardPayment> getCardPayement(String type);
}