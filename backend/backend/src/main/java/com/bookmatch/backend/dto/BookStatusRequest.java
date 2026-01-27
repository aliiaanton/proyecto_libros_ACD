package com.bookmatch.backend.dto;

import com.bookmatch.backend.enums.ReadingStatusType;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para las peticiones de actualización del estado de lectura.
 * Permite al usuario actualizar en qué estado se encuentra con respecto a un libro.
 */
@Data
public class BookStatusRequest {
    /** ID del usuario (temporal, será automático por token en el futuro) */
    private Long userId;

    /** ID del libro en Google Books */
    private String googleBookId;

    /** Estado de lectura (WANT_TO_READ, READING, READ, DROPPED) */
    private ReadingStatusType status;
}