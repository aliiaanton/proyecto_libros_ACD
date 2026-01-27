package com.bookmatch.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder // Patrón Builder para crear objetos fácilmente
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "google_book_id", nullable = false, unique = true)
    private String googleBookId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String authors;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String isbn;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "published_date")
    private String publishedDate;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "average_rating_api")
    private Double averageRatingApi;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relación ManyToMany con Genres
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    /** Etiquetas específicas asociadas al libro (relación muchos a muchos) */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_tags",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    /**
     * Establece la fecha de creación del libro antes de persistirlo en la base de datos.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}