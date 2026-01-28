package com.bookmatch.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO enriquecida para respuestas de libros.
 * Incluye información del libro con sus géneros, tags, reseñas y estado de lectura del usuario.
 */
@Data
@Builder
public class BookResponse {
    /** ID del libro en la base de datos local */
    private Long bookId;

    /** ID del libro en Google Books */
    private String googleBookId;

    /** Título del libro */
    private String title;

    /** Autores del libro */
    private String authors;

    /** Descripción del libro */
    private String description;

    /** ISBN del libro */
    private String isbn;

    /** Número de páginas */
    private Integer pageCount;

    /** Fecha de publicación */
    private String publishedDate;

    /** URL de la portada */
    private String coverUrl;

    /** Calificación promedio de la API de Google Books */
    private Double averageRatingApi;

    /** Nombres de los géneros del libro */
    private List<String> genres;

    /** Nombres de las etiquetas del libro */
    private List<String> tags;

    /** Calificación promedio de la comunidad BookMatch */
    private Double communityRating;

    /** Número total de reseñas del libro */
    private Long totalReviews;

    /** Estado de lectura del usuario autenticado (WANT_TO_READ, READING, READ, DROPPED, null si no tiene) */
    private String userReadingStatus;

    /** Si el usuario actual ha reseñado este libro, aquí va su reseña */
    private ReviewResponse userReview;
}
