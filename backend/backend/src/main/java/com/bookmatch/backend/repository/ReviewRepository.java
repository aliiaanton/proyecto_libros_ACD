package com.bookmatch.backend.repository;

import com.bookmatch.backend.entity.Review;
import com.bookmatch.backend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la gestión de reseñas de libros en la base de datos.
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * Obtiene todas las reseñas de un libro específico.
     *
     * @param book Libro del cual obtener las reseñas.
     * @return Lista de reseñas del libro.
     */
    List<Review> findByBook(Book book);
    List<Review> findByUser_UserIdAndRatingGreaterThanEqual(Long userId, Integer rating);
}