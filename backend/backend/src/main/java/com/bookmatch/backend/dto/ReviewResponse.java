package com.bookmatch.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO enriquecida para respuestas de reseñas.
 * Incluye información completa de la reseña con datos del usuario que la escribió.
 */
@Data
@Builder
public class ReviewResponse {
    /** ID de la reseña */
    private Long reviewId;

    /** Nombre de usuario de quien escribió la reseña */
    private String username;

    /** Bio del usuario */
    private String userBio;

    /** Calificación de 1 a 5 */
    private Integer rating;

    /** Comentario de la reseña */
    private String comment;

    /** Puntuación de sentimiento analizada por LLM (-1 a 1) */
    private Double sentimentScore;

    /** Palabras clave extraídas por LLM */
    private String keywords;

    /** Fecha de creación de la reseña */
    private LocalDateTime createdAt;
}
