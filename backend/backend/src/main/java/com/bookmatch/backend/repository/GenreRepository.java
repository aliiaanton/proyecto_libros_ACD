package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la gestión de géneros literarios en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    /**
     * Busca un género por su nombre.
     *
     * @param name Nombre del género.
     * @return Optional con el género si existe.
     */
    Optional<Genre> findByName(String name);
}