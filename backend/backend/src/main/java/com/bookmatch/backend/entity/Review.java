package com.bookmatch.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una reseña de un libro realizada por un usuario.
 * Incluye calificación, comentario y análisis de sentimiento por LLM.
 */
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Review {

    /** ID único de la reseña */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    /** Usuario que escribió la reseña */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Libro reseñado */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /** Calificación del libro (1 a 5 estrellas) */
    @Column(nullable = false)
    private Integer rating;

    /** Comentario o texto de la reseña */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /** Puntuación de sentimiento analizada por LLM */
    @Column(name = "llm_sentiment_score")
    private Double llmSentimentScore;

    /** Palabras clave extraídas por LLM en formato JSON */
    @Column(name = "llm_keywords", columnDefinition = "JSON")
    private String llmKeywords;

    /** Fecha de creación de la reseña */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Establece la fecha de creación antes de persistir la reseña.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}