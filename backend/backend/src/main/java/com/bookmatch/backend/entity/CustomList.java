package com.bookmatch.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una lista personalizada de libros creada por un usuario.
 * Permite a los usuarios organizar sus libros en colecciones temáticas.
 */
@Entity
@Table(name = "custom_lists")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomList {

    /** ID único de la lista personalizada */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private Long listId;

    /** Usuario propietario de la lista */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Nombre de la lista */
    @Column(nullable = false, length = 100)
    private String name;

    /** Descripción de la lista */
    private String description;

    /** Indica si la lista es pública o privada */
    @Column(name = "is_public")
    private Boolean isPublic;

    /** Fecha de creación de la lista */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Libros incluidos en la lista (relación muchos a muchos) */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "custom_list_books",
            joinColumns = @JoinColumn(name = "list_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books = new HashSet<>();

    /**
     * Establece valores por defecto antes de persistir la lista.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isPublic == null) isPublic = true;
    }
}