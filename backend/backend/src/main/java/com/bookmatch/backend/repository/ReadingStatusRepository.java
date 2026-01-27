package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.ReadingStatus;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.enums.ReadingStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de estados de lectura en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface ReadingStatusRepository extends JpaRepository<ReadingStatus, Long> {
    /**
     * Obtiene todos los estados de lectura de un usuario.
     *
     * @param user Usuario del cual obtener los estados.
     * @return Lista de estados de lectura del usuario.
     */
    List<ReadingStatus> findByUser(User user);

    /**
     * Busca el estado de lectura específico de un usuario para un libro.
     *
     * @param user Usuario propietario del estado.
     * @param book Libro asociado al estado.
     * @return Optional con el estado de lectura si existe.
     */
    Optional<ReadingStatus> findByUserAndBook(User user, Book book);

    /**
     * Obtiene todos los libros de un usuario filtrados por un estado específico.
     *
     * @param user Usuario del cual obtener los libros.
     * @param status Estado de lectura a filtrar.
     * @return Lista de estados de lectura que coinciden con el filtro.
     */
    List<ReadingStatus> findByUserAndStatus(User user, ReadingStatusType status);
}