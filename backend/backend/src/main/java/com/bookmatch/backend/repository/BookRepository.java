package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de datos de la entidad Book.
 * Extiende JpaRepository para proporcionar operaciones CRUD estándar y consultas personalizadas.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Busca un libro en la base de datos local utilizando el ID de Google Books.
     *
     * @param googleBookId El identificador único proporcionado por la API de Google.
     * @return Un Optional que contiene el libro si se encuentra, o vacío si no.
     */
    Optional<Book> findByGoogleBookId(String googleBookId);

    /**
     * Algoritmo de Recomendación:
     * Busca libros que pertenezcan a una lista de géneros específicos,
     * EXCLUYENDO aquellos que el usuario ya tiene en su lista de lectura (leídos, pendientes, etc.).
     *
     * @param genres Lista de nombres de géneros (Strings) en los que buscar.
     * @param userId El ID del usuario para excluir sus libros ya guardados.
     * @return Lista de libros recomendados que coinciden con los criterios.
     */
    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN b.genres g " +
            "WHERE g.name IN :genres " +
            "AND b.bookId NOT IN (" +
            "    SELECT rs.book.bookId FROM ReadingStatus rs WHERE rs.user.userId = :userId" +
            ")")
    List<Book> findRecommendations(@Param("genres") List<String> genres, @Param("userId") Long userId);
}