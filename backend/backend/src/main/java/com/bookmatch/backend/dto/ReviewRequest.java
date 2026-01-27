package com.bookmatch.backend.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) para las peticiones de creación o actualización de reseñas.
 * Contiene los datos necesarios para que un usuario reseñe un libro.
 */
@Data
public class ReviewRequest {
    /** ID del usuario que escribe la reseña (temporal, será automático por token en el futuro) */
    private Long userId;

    /** ID del libro en Google Books */
    private String googleBookId;

    /** Calificación del libro (1 a 5 estrellas) */
    private Integer rating;

    /** Comentario o texto de la reseña */
    private String comment;
}