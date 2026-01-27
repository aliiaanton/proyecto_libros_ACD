package com.bookmatch.backend.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para las respuestas de la funcionalidad "Cita a Ciegas".
 * Contiene una cita de un libro junto con pistas sobre el género, sin revelar el título inicialmente.
 */
@Data
@Builder
public class BlindDateResponse {
    /** ID de la cita en la base de datos */
    private Long quoteId;

    /** Texto de la cita del libro */
    private String quoteText;

    /** ID del libro en Google Books (para que el frontend pueda revelar la portada después) */
    private String googleBookId;

    /** Género del libro como pista (ej: "Fantasía") */
    private String genre;
}