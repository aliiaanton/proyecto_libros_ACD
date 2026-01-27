package com.bookmatch.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

/**
 * Entidad que representa las preferencias de géneros literarios de un usuario.
 * Almacena un peso o puntuación que indica cuánto le gusta cada género al usuario.
 */
@Entity
@Table(name = "user_genre_preferences")
@IdClass(UserGenrePreference.UserGenreId.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserGenrePreference {

    /** ID del usuario (parte de la clave compuesta) */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /** ID del género (parte de la clave compuesta) */
    @Id
    @Column(name = "genre_id")
    private Long genreId;

    /** Peso o puntuación de preferencia del usuario hacia este género */
    private Integer weight;

    /** Referencia al usuario */
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /** Referencia al género */
    @ManyToOne
    @JoinColumn(name = "genre_id", insertable = false, updatable = false)
    private Genre genre;

    /**
     * Clase interna que representa la clave compuesta de UserGenrePreference.
     * Combina userId y genreId como clave primaria compuesta.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGenreId implements Serializable {
        private Long userId;
        private Long genreId;
    }
}