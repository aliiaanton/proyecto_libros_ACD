package com.bookmatch.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una etiqueta o tag para clasificar libros.
 * Permite añadir etiquetas específicas más allá de los géneros (ej: "enemies-to-lovers", "viajes en el tiempo").
 */
@Entity
@Table(name = "tags")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Tag {

    /** ID único de la etiqueta */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    /** Nombre de la etiqueta */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** Slug de la etiqueta para URLs amigables */
    @Column(nullable = false, length = 50)
    private String slug;

    /** Descripción de la etiqueta */
    @Column(length = 200)
    private String description;

    /** Libros asociados a esta etiqueta (relación inversa) */
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<Book> books = new HashSet<>();
}