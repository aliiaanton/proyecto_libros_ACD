package com.bookmatch.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO para respuestas de recomendaciones con puntuación.
 * Incluye un libro recomendado junto con su puntuación numérica (0-100)
 * que indica qué tan bien encaja con las preferencias del usuario.
 */
@Data
@Builder
public class RecommendationResponse {
    /** Información del libro recomendado */
    private BookResponse book;

    /** Puntuación de recomendación (0-100) donde 100 es la mejor coincidencia */
    private Double score;

    /** Razones por las que se recomienda (para mayor transparencia) */
    private List<String> reasons;

    /** Desglose de puntuación para referencia */
    private ScoreBreakdown scoreBreakdown;

    @Data
    @Builder
    public static class ScoreBreakdown {
        /** Puntuación por coincidencia de géneros (0-40) */
        private Double genreMatch;

        /** Puntuación por reseñas altas (0-30) */
        private Double highRatings;

        /** Puntuación por tendencias recientes (0-20) */
        private Double trendingScore;

        /** Puntuación por popularidad general (0-10) */
        private Double popularity;
    }
}
