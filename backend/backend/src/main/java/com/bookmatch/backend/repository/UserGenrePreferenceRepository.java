package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.UserGenrePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la gestión de preferencias de géneros de usuarios en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface UserGenrePreferenceRepository extends JpaRepository<UserGenrePreference, UserGenrePreference.UserGenreId> {
    /**
     * Obtiene todas las preferencias de géneros de un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de preferencias de géneros del usuario.
     */
    List<UserGenrePreference> findByUserId(Long userId);
}