package com.bookmatch.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO para respuestas de la página principal (Home).
 * Incluye resumen de libros destacados, recomendaciones y datos del usuario.
 */
@Data
@Builder
public class HomeResponse {
    /** Libros más populares o destacados */
    private List<BookResponse> featuredBooks;

    /** Recomendaciones personalizadas para el usuario (si está autenticado) */
    private List<RecommendationResponse> personalRecommendations;

    /** Géneros principales disponibles en la plataforma */
    private List<GenreDTO> mainGenres;

    /** Tags principales disponibles en la plataforma */
    private List<TagDTO> mainTags;

    @Data
    @Builder
    public static class GenreDTO {
        private Long genreId;
        private String name;
        private Long bookCount;
    }

    @Data
    @Builder
    public static class TagDTO {
        private Long tagId;
        private String name;
        private Long bookCount;
    }
}
