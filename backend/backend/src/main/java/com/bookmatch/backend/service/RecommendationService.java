package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.BlindDateResponse;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.BookQuote;
import com.bookmatch.backend.entity.Genre;
import com.bookmatch.backend.entity.Review;
import com.bookmatch.backend.repository.BookQuoteRepository;
import com.bookmatch.backend.repository.BookRepository;
import com.bookmatch.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la lógica de negocio para las recomendaciones de libros.
 * Incluye funcionalidades para la "Cita a Ciegas" y recomendaciones personalizadas
 * basadas en el historial del usuario.
 */
@Service
public class RecommendationService {

    @Autowired
    private BookQuoteRepository bookQuoteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Obtiene una "Cita a Ciegas" con un libro.
     * Selecciona una frase aleatoria de la base de datos para mostrarla al usuario
     * sin revelar inicialmente la identidad del libro.
     *
     * @return DTO BlindDateResponse con la frase, el ID oculto y el género.
     * @throws RuntimeException Si no hay citas disponibles en la base de datos.
     */
    public BlindDateResponse getBlindDate() {
        // 1. Sacar una cita aleatoria de la BD
        BookQuote quote = bookQuoteRepository.findRandomQuote();

        if (quote == null) {
            throw new RuntimeException("¡No hay citas disponibles en la base de datos! Añade algunas primero.");
        }

        Book book = quote.getBook();

        // 2. Extraer el género principal como pista (si tiene)
        String genreName = "Desconocido";
        if (!book.getGenres().isEmpty()) {
            // Cogemos el nombre del primer género que tenga
            genreName = book.getGenres().iterator().next().getName();
        }

        // 3. Devolver el DTO
        return BlindDateResponse.builder()
                .quoteId(quote.getQuoteId())
                .quoteText(quote.getQuoteText())
                .googleBookId(book.getGoogleBookId()) // El ID secreto para revelar luego
                .genre(genreName)
                .build();
    }

    /**
     * Genera recomendaciones personalizadas para un usuario.
     * Analiza las reseñas del usuario con puntuación alta (4 o 5 estrellas),
     * extrae los géneros favoritos y busca libros similares no leídos.
     *
     * @param userId El ID del usuario que solicita la recomendación.
     * @return Una lista de hasta 10 libros recomendados.
     */
    public List<Book> getPersonalizedRecommendations(Long userId) {
        // 1. Obtener reseñas positivas del usuario (4 o 5 estrellas)
        List<Review> userReviews = reviewRepository.findAll().stream()
                .filter(r -> r.getUser().getUserId().equals(userId))
                .filter(r -> r.getRating() >= 4)
                .toList();

        // FALLBACK: Si no tiene reseñas positivas, devolvemos libros genéricos
        if (userReviews.isEmpty()) {
            return bookRepository.findAll().stream().limit(5).toList();
        }

        // 2. Extraer los nombres de los géneros que le gustan
        List<String> favoriteGenres = userReviews.stream()
                .flatMap(r -> r.getBook().getGenres().stream()) // Sacamos los géneros de cada libro
                .map(Genre::getName) // Nos quedamos con el nombre
                .distinct() // Quitamos duplicados
                .toList();

        if (favoriteGenres.isEmpty()) {
            return bookRepository.findAll().stream().limit(5).toList();
        }

        // 3. Buscar libros similares en la BD excluyendo los que ya tiene guardados
        List<Book> recommendations = bookRepository.findRecommendations(favoriteGenres, userId);

        // Devolver máximo 10 resultados
        return recommendations.stream().limit(10).toList();
    }
}