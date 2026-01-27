package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.CustomList;
import com.bookmatch.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la gestión de listas personalizadas de libros en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface CustomListRepository extends JpaRepository<CustomList, Long> {
    /**
     * Obtiene todas las listas personalizadas de un usuario.
     *
     * @param user Usuario propietario de las listas.
     * @return Lista de listas personalizadas del usuario.
     */
    List<CustomList> findByUser(User user);

    /**
     * Obtiene todas las listas públicas del sistema.
     *
     * @return Lista de listas personalizadas públicas.
     */
    List<CustomList> findByIsPublicTrue();
}