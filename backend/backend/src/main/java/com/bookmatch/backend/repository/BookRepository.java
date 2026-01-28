package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByGoogleBookId(String googleBookId);

    /**
     * Algoritmo de Recomendación:
     * Busca libros por lista de géneros, EXCLUYENDO los que el usuario ya tiene en su lista.
     */
    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN b.genres g " +
            "WHERE g.name IN :genres " +
            "AND b.bookId NOT IN (" +
            "    SELECT rs.book.bookId FROM ReadingStatus rs WHERE rs.user.userId = :userId" +
            ")")
    List<Book> findRecommendations(@Param("genres") List<String> genres, @Param("userId") Long userId);

    /**
     * Fallback: Devuelve libros aleatorios si no hay datos del usuario.
     * Nota: ORDER BY RAND() es para MySQL. Si usas PostgreSQL cambia RAND() por RANDOM().
     */
    @Query(value = "SELECT * FROM books ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Book> findRandomBooks(@Param("limit") int limit);
}