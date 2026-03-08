package com.giteck.academy.heritage.repository;

import com.giteck.academy.heritage.entity.User; // A supposer que l'entité existe
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // L'EntityGraph indique le plan de chargement attendu pour éviter le N+1
    @EntityGraph(attributePaths = {"payments"})
    List<User> findAll();
}