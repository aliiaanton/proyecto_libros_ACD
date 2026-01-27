package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la gestión de usuarios en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @return Optional con el usuario si existe.
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario.
     * @return Optional con el usuario si existe.
     */
    Optional<User> findByUsername(String username);

    /**
     * Verifica si existe un usuario con el correo electrónico especificado.
     *
     * @param email Correo electrónico a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario especificado.
     *
     * @param username Nombre de usuario a verificar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByUsername(String username);

    /**
     * Busca un usuario por su token de verificación.
     *
     * @param token Token de verificación del usuario.
     * @return Optional con el usuario si existe.
     */
    Optional<User> findByVerificationToken(String token);
}