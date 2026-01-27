package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.UserTagPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la gestión de preferencias de etiquetas de usuarios en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface UserTagPreferenceRepository extends JpaRepository<UserTagPreference, UserTagPreference.UserTagId> {
    /**
     * Obtiene todas las preferencias de etiquetas de un usuario.
     *
     * @param userId ID del usuario.
     * @return Lista de preferencias de etiquetas del usuario.
     */
    List<UserTagPreference> findByUserId(Long userId);
}