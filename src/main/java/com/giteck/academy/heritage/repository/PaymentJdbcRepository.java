package com.giteck.academy.heritage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public class PaymentJdbcRepository {

    private final JdbcTemplate jdbc;

    public PaymentJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public BigDecimal getTotalPaymentsAmount() {
        String sql = "SELECT SUM(amount) FROM payment";
        // Utilisation de queryForObject pour extraire une valeur simple
        BigDecimal total = jdbc.queryForObject(sql, BigDecimal.class);
        return total != null ? total : BigDecimal.ZERO;
    }
}