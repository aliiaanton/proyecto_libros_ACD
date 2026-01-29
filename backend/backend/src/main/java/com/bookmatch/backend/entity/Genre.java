package com.bookmatch.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa un género literario.
 * Permite clasificar libros por género (ej: Fantasía, Romance, Terror).
 */
@Entity
@Table(name = "genres")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Genre {

    /** ID único del género */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "genre_id")
    private Long genreId;

    /** Nombre del género */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** Slug del género para URLs amigables */
    @Column(nullable = false, length = 50)
    private String slug;

    /** Libros asociados a este género (relación inversa) */
    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Book> books = new HashSet<>();
}