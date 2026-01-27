package com.bookmatch.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una cita o frase memorable de un libro.
 * Se utiliza para la funcionalidad "Cita a Ciegas" y para mostrar fragmentos destacados.
 */
@Entity
@Table(name = "book_quotes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BookQuote {

    /** ID único de la cita */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Long quoteId;

    /** Libro al que pertenece la cita */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /** Texto de la cita */
    @Column(name = "quote_text", nullable = false, columnDefinition = "TEXT")
    private String quoteText;

    /** Origen de la cita (MANUAL o AI_EXTRACTED) */
    private String source;
}