package com.bookmatch.backend.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO (Data Transfer Object) para las peticiones de registro de nuevos usuarios.
 * Contiene los datos necesarios para crear una nueva cuenta de usuario,
 * incluyendo preferencias iniciales de géneros.
 */
@Data
public class RegisterRequest {
    /** Nombre de usuario deseado */
    private String username;

    /** Correo electrónico del usuario */
    private String email;

    /** Contraseña del usuario (será encriptada antes de guardarla) */
    private String password;

    /** Lista de IDs de géneros preferidos por el usuario */
    private List<Long> genrePreferenceIds;

    /** Lista de IDs de etiquetas preferidas por el usuario */
    private List<Long> tagPreferenceIds;
}