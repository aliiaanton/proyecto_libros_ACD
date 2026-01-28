package com.bookmatch.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO para respuestas de listas personalizadas.
 * Incluye información de la lista con los libros que contiene.
 */
@Data
@Builder
public class CustomListResponse {
    /** ID de la lista */
    private Long listId;

    /** Nombre de la lista */
    private String name;

    /** Descripción de la lista */
    private String description;

    /** Indica si la lista es pública */
    private Boolean isPublic;

    /** Número de libros en la lista */
    private Integer bookCount;

    /** Libros en la lista (respuestas enriquecidas) */
    private List<BookResponse> books;
}
