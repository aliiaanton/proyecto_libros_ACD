package com.bookmatch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitudes de creación o actualización de listas personalizadas.
 * Contiene los datos necesarios para que un usuario cree una nueva lista.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomListRequest {
    /** Nombre de la lista personalizada */
    private String name;

    /** Descripción opcional de la lista */
    private String description;

    /** Indica si la lista es pública o privada */
    private Boolean isPublic;
}
