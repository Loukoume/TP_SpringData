package com.giteck.academy.heritage.controller;

import com.giteck.academy.heritage.entity.User;
import com.giteck.academy.heritage.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller // DANGER: Ne pas utiliser @RestController ici, sinon Spring tentera de convertir ModelAndView en JSON !
public class UserViewController {

    private final UserRepository userRepository;

    public UserViewController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users/{id}/profile")
    public ModelAndView getUserProfile(@PathVariable Long id) {
        // 1. Instanciation avec le nom logique de la vue
        // Spring cherchera un fichier nommé "user-profile.html" (si Thymeleaf est utilisé) dans src/main/resources/templates/
        ModelAndView mav = new ModelAndView("user-profile");

        // 2. Récupération des données (le Modèle)
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // 3. Ajout des données à la vue
            // La clé ("user", "paymentList") sera la variable utilisable dans le fichier HTML
            mav.addObject("user", user);
            mav.addObject("paymentList", user.getPayments()); // Rappel : la classe User a une liste de paiements

        } else {
            // Si l'utilisateur n'existe pas, on modifie la vue dynamiquement pour afficher une page d'erreur
            mav.setViewName("error-404");
            mav.addObject("message", "Désolé, l'utilisateur avec l'ID " + id + " est introuvable.");
        }

        // 4. On retourne la combinaison (Vue + Modèle)
        return mav;
    }
}