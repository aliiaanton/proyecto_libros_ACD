package com.bookmatch.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

/**
 * Entidad que representa las preferencias de etiquetas de un usuario.
 * Almacena un peso o puntuación que indica cuánto le gusta cada etiqueta al usuario.
 */
@Entity
@Table(name = "user_tag_preferences")
@IdClass(UserTagPreference.UserTagId.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserTagPreference {

    /** ID del usuario (parte de la clave compuesta) */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /** ID de la etiqueta (parte de la clave compuesta) */
    @Id
    @Column(name = "tag_id")
    private Long tagId;

    /** Peso o puntuación de preferencia del usuario hacia esta etiqueta */
    private Integer weight;

    /** Referencia al usuario */
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /** Referencia a la etiqueta */
    @ManyToOne
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    private Tag tag;

    /**
     * Clase interna que representa la clave compuesta de UserTagPreference.
     * Combina userId y tagId como clave primaria compuesta.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTagId implements Serializable {
        private Long userId;
        private Long tagId;
    }
}