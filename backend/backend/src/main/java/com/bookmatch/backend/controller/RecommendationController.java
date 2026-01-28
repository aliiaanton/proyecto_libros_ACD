package com.bookmatch.backend.controller;

import com.bookmatch.backend.dto.BlindDateResponse;
import com.bookmatch.backend.dto.RecommendationResponse;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.UserRepository;
import com.bookmatch.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para gestionar las solicitudes de recomendaciones.
 * Expone endpoints para obtener sugerencias personalizadas y la funcionalidad de "Cita a Ciegas".
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Endpoint público para obtener una "Cita a Ciegas" (Libro oculto con una frase).
     * No requiere autenticación.
     *
     * @return ResponseEntity con la información de la cita o un error si no hay datos.
     */
    @GetMapping("/blind-date")
    public ResponseEntity<?> getBlindDate() {
        try {
            BlindDateResponse response = recommendationService.getBlindDate();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * Endpoint público para obtener una "Cita a Ciegas" filtrada por etiqueta.
     * No requiere autenticación.
     *
     * @param tagId ID de la etiqueta para filtrar
     * @return ResponseEntity con la información de la cita filtrada
     */
    @GetMapping("/blind-date/tag/{tagId}")
    public ResponseEntity<?> getBlindDateByTag(@PathVariable Long tagId) {
        try {
            BlindDateResponse response = recommendationService.getBlindDateByTag(tagId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    /**
     * Endpoint protegido para obtener recomendaciones personalizadas CON PUNTUACIÓN.
     * Utiliza el Token JWT del usuario autenticado para identificar sus preferencias.
     *
     * @return Lista de recomendaciones con puntuación numérica
     */
    @GetMapping("/personal/scored")
    public ResponseEntity<?> getPersonalRecommendationsWithScore() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<RecommendationResponse> recommendations =
                    recommendationService.getPersonalizedRecommendationsWithScore(user.getUserId());

            return ResponseEntity.ok(recommendations);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    /**
     * Endpoint protegido para obtener recomendaciones personalizadas (legacy, sin puntuación).
     * Utiliza el Token JWT del usuario autenticado para identificar sus preferencias.
     *
     * @return Lista de libros recomendados basada en las reseñas previas del usuario.
     */
    @GetMapping("/personal")
    public ResponseEntity<?> getPersonalRecommendations() {
        // 1. Sacar el email del usuario del contexto de seguridad (JWT)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        // 2. Buscar el usuario en la BD para obtener su ID real
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Pedir recomendaciones al servicio usando el ID del usuario
        List<Book> books = recommendationService.getPersonalizedRecommendations(user.getUserId());

        return ResponseEntity.ok(books);
    }
}