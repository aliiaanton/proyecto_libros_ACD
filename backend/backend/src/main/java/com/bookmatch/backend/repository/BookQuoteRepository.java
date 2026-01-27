package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.BookQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de citas de libros en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface BookQuoteRepository extends JpaRepository<BookQuote, Long> {

    /**
     * Obtiene una cita aleatoria de la base de datos.
     * Utilizado para la funcionalidad "Cita a Ciegas".
     *
     * @return Una cita aleatoria de la base de datos.
     */
    @Query(value = "SELECT * FROM book_quotes ORDER BY RAND() LIMIT 1", nativeQuery = true)
    BookQuote findRandomQuote();
}