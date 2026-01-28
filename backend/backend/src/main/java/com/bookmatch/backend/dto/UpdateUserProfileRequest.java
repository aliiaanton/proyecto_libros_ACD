package com.bookmatch.backend.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para solicitudes de edición del perfil del usuario.
 * Permite actualizar preferencias y información del usuario.
 */
@Data
@Builder
public class UpdateUserProfileRequest {
    /** Bio del usuario */
    private String bio;

    /** IDs de géneros preferidos (actualiza las preferencias) */
    private java.util.List<Long> genrePreferenceIds;

    /** IDs de tags preferidos (actualiza las preferencias) */
    private java.util.List<Long> tagPreferenceIds;
}
