package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.ReviewRequest;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.Review;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.ReviewRepository;
import com.bookmatch.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    // Crear o Actualizar reseña
    public Review saveReview(ReviewRequest request) {
        // 1. Validar Usuario
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Asegurar Libro (Si no está, lo baja de Google)
        Book book = bookService.findOrSaveBookFromGoogle(request.getGoogleBookId());
        if (book == null) throw new RuntimeException("Libro no válido");

        // 3. Validar Rating (1-5)
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("La puntuación debe ser entre 1 y 5");
        }

        // 4. Comprobar si ya existe reseña (para editarla en vez de crear otra)
        // Como pusimos una restricción UNIQUE en la BD, mejor buscamos antes.
        // Nota: ReviewRepository necesita un método para buscar por User y Book, vamos a crearlo si falta.
        // Pero espera, JPA es listo. Vamos a usar un truco con Stream si no queremos tocar el Repo ahora,
        // aunque lo ideal es añadir 'findByUserAndBook' en el repositorio.

        // Vamos a asumir que editamos si ya existe
        Optional<Review> existingReview = reviewRepository.findAll().stream()
                .filter(r -> r.getUser().getUserId().equals(user.getUserId())
                        && r.getBook().getBookId().equals(book.getBookId()))
                .findFirst();

        Review review;
        if (existingReview.isPresent()) {
            review = existingReview.get();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            // Aquí se recalcularía el sentimiento con IA en el futuro
        } else {
            review = Review.builder()
                    .user(user)
                    .book(book)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
        }

        return reviewRepository.save(review);
    }

    /**
     * Obtiene todas las reseñas de un libro específico.
     *
     * @param googleBookId El ID del libro en Google Books.
     * @return Lista de reseñas del libro, o lista vacía si el libro no existe en la base de datos.
     */
    public List<Review> getReviewsByBook(String googleBookId) {
        // Primero buscamos el libro en nuestra BD
        Book book = bookService.getBookByGoogleId(googleBookId);
        if (book == null) return List.of(); // Si no existe en nuestra BD, no tiene reseñas

        return reviewRepository.findByBook(book);
    }
}