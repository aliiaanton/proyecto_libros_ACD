package com.bookmatch.backend.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) para las peticiones de registro de nuevos usuarios.
 * Contiene los datos necesarios para crear una nueva cuenta de usuario.
 */
@Data
public class RegisterRequest {
    /** Nombre de usuario deseado */
    private String username;

    /** Correo electrónico del usuario */
    private String email;

    /** Contraseña del usuario (será encriptada antes de guardarla) */
    private String password;
}