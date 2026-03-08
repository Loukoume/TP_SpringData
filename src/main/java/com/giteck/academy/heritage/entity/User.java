package com.giteck.academy.heritage.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private boolean active = true;

    // Relation bidirectionnelle vers Payment (côté inverse/mappedBy) [cite: 171, 173]
    // Utilisé dans le précédent TP pour illustrer le problème N+1 et @EntityGraph [cite: 213, 214, 215, 223]
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    // Constructeur par défaut obligatoire pour JPA
    public User() {
    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    // --- Méthodes utilitaires de synchronisation (Bonne pratique du cours) --- [cite: 176, 177, 178, 179, 180]

    public void addPayment(Payment payment) {
        payments.add(payment);
        // /!\ Nécessite d'ajouter un champ 'private User user;' dans la classe Payment
        // payment.setUser(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        // payment.setUser(null);
    }
}
