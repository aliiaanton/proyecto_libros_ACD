package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la gestión de etiquetas en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    /**
     * Busca una etiqueta por su nombre.
     *
     * @param name Nombre de la etiqueta.
     * @return Optional con la etiqueta si existe.
     */
    Optional<Tag> findByName(String name);
}