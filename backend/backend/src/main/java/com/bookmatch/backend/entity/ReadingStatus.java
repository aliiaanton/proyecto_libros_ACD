package com.bookmatch.backend.entity;

import com.bookmatch.backend.enums.ReadingStatusType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa el estado de lectura de un libro para un usuario.
 * Permite llevar el seguimiento de qué libros quiere leer, está leyendo, ha leído o abandonado.
 */
@Entity
@Table(name = "reading_status", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReadingStatus {

    /** ID único del registro de estado de lectura */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    /** Usuario al que pertenece este estado de lectura */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Libro asociado al estado de lectura */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /** Estado de lectura actual (WANT_TO_READ, READING, READ, DROPPED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingStatusType status;

    /** Fecha de última actualización del estado */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Actualiza la fecha de última modificación al crear o actualizar el registro.
     */
    @PrePersist @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}