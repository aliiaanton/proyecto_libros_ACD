package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.BlindDateResponse;
import com.bookmatch.backend.dto.RecommendationResponse;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.BookQuote;
import com.bookmatch.backend.entity.Genre;
import com.bookmatch.backend.entity.Review;
import com.bookmatch.backend.entity.Tag;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.BookQuoteRepository;
import com.bookmatch.backend.repository.BookRepository;
import com.bookmatch.backend.repository.ReviewRepository;
import com.bookmatch.backend.repository.TagRepository;
import com.bookmatch.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private BookQuoteRepository bookQuoteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * LÓGICA DE CITA A CIEGAS
     * Obtiene una cita aleatoria de un libro
     */
    public BlindDateResponse getBlindDate() {
        BookQuote quote = bookQuoteRepository.findRandomQuote();

        if (quote == null) {
            throw new RuntimeException("No hay citas disponibles. ¡Añade algunas frases a la base de datos!");
        }

        Book book = quote.getBook();

        // Intentamos obtener el nombre del género, si existe
        String genreName = "Misterio";
        if (!book.getGenres().isEmpty()) {
            genreName = book.getGenres().iterator().next().getName();
        }

        return BlindDateResponse.builder()
                .quoteId(quote.getQuoteId())
                .quoteText(quote.getQuoteText())
                .googleBookId(book.getGoogleBookId())
                .genre(genreName)
                .build();
    }

    /**
     * Obtiene una "Cita a Ciegas" filtrada por una etiqueta específica
     * Devuelve solo una frase del libro, ocultando el resto de la información
     */
    public BlindDateResponse getBlindDateByTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada"));

        // Obtener libros asociados a esta etiqueta que tengan citas
        List<BookQuote> quotes = new ArrayList<>();
        for (Book book : tag.getBooks()) {
            List<BookQuote> bookQuotes = bookQuoteRepository.findByBook(book);
            quotes.addAll(bookQuotes);
        }

        if (quotes.isEmpty()) {
            throw new RuntimeException("No hay citas disponibles para esta etiqueta");
        }

        // Seleccionar una cita aleatoria
        BookQuote quote = quotes.get((int) (Math.random() * quotes.size()));
        Book book = quote.getBook();

        String genreName = "Varios";
        if (!book.getGenres().isEmpty()) {
            genreName = book.getGenres().iterator().next().getName();
        }

        return BlindDateResponse.builder()
                .quoteId(quote.getQuoteId())
                .quoteText(quote.getQuoteText())
                .googleBookId(book.getGoogleBookId())
                .genre(genreName)
                .build();
    }

    /**
     * LÓGICA DE RECOMENDACIÓN PERSONALIZADA CON PUNTUACIÓN
     * Mezcla: Preferencias del perfil + Géneros de libros bien valorados.
     * Calcula una puntuación (0-100) para cada libro basada en múltiples factores.
     */
    public List<RecommendationResponse> getPersonalizedRecommendationsWithScore(Long userId) {
        // 1. Obtener el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Usamos un Set para evitar géneros duplicados
        Set<String> targetGenres = new HashSet<>();

        // A. Añadir géneros seleccionados en el perfil (Formulario inicial)
        if (user.getGenrePreferences() != null) {
            user.getGenrePreferences().forEach(pref ->
                    targetGenres.add(pref.getGenre().getName())
            );
        }

        // B. Añadir géneros de libros que el usuario ha valorado con 4 o 5 estrellas
        List<Review> goodReviews = reviewRepository.findByUser_UserIdAndRatingGreaterThanEqual(userId, 4);
        for (Review review : goodReviews) {
            review.getBook().getGenres().forEach(g -> targetGenres.add(g.getName()));
        }

        // 2. Si no sabemos nada del usuario (ni perfil ni reseñas), devolvemos aleatorios (Cold Start)
        if (targetGenres.isEmpty()) {
            List<Book> randomBooks = bookRepository.findRandomBooks(10);
            return randomBooks.stream()
                    .map(book -> createRecommendationResponse(book, 50.0, null))
                    .collect(Collectors.toList());
        }

        // 3. Buscar libros en la BD que coincidan con esos géneros y NO haya leído
        List<String> genreList = new ArrayList<>(targetGenres);
        List<Book> recommendations = bookRepository.findRecommendations(genreList, userId);

        // Si la búsqueda personalizada devuelve muy pocos resultados, rellenamos con aleatorios
        if (recommendations.size() < 5) {
            List<Book> filler = bookRepository.findRandomBooks(5);
            // Añadimos solo los que no estén ya en la lista (evitar duplicados simples)
            for (Book b : filler) {
                if (!recommendations.contains(b)) {
                    recommendations.add(b);
                }
            }
        }

        // 4. Calcular puntuaciones para cada libro
        List<RecommendationResponse> recommendationsWithScores = recommendations.stream()
                .map(book -> {
                    Double score = calculateRecommendationScore(book, userId, targetGenres);
                    List<String> reasons = generateRecommendationReasons(book, userId, targetGenres);
                    return createRecommendationResponse(book, score, reasons);
                })
                .collect(Collectors.toList());

        // 5. Ordenar por puntuación (mayor primero) y limitar a 10 resultados
        return recommendationsWithScores.stream()
                .sorted(Comparator.comparingDouble(RecommendationResponse::getScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Calcula la puntuación de recomendación para un libro (0-100)
     * Basada en: coincidencia de géneros (40%), reseñas altas (30%), tendencias (20%), popularidad (10%)
     */
    private Double calculateRecommendationScore(Book book, Long userId, Set<String> targetGenres) {
        double totalScore = 0.0;

        // 1. Coincidencia de géneros (40 puntos máximo)
        double genreScore = calculateGenreMatchScore(book, targetGenres);
        totalScore += genreScore * 0.4;

        // 2. Reseñas altas de otros usuarios (30 puntos máximo)
        double ratingScore = calculateRatingScore(book);
        totalScore += ratingScore * 0.3;

        // 3. Tendencias recientes (20 puntos máximo)
        double trendingScore = calculateTrendingScore(book);
        totalScore += trendingScore * 0.2;

        // 4. Popularidad general (10 puntos máximo)
        double popularityScore = calculatePopularityScore(book);
        totalScore += popularityScore * 0.1;

        return Math.min(100.0, totalScore);
    }

    /**
     * Calcula la puntuación por coincidencia de géneros (0-100)
     */
    private Double calculateGenreMatchScore(Book book, Set<String> targetGenres) {
        if (targetGenres.isEmpty()) return 0.0;

        Set<String> bookGenres = book.getGenres().stream()
                .map(Genre::getName)
                .collect(Collectors.toSet());

        long matches = bookGenres.stream()
                .filter(targetGenres::contains)
                .count();

        return (double) (matches * 100 / targetGenres.size());
    }

    /**
     * Calcula la puntuación por reseñas altas (0-100)
     */
    private Double calculateRatingScore(Book book) {
        List<Review> reviews = reviewRepository.findByBook(book);

        if (reviews.isEmpty()) return 50.0; // Puntuación neutral si no hay reseñas

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // Convertir de escala 1-5 a 0-100
        return (averageRating / 5.0) * 100.0;
    }

    /**
     * Calcula la puntuación por tendencias recientes (0-100)
     * Basada en reseñas recientes (últimos 30 días)
     */
    private Double calculateTrendingScore(Book book) {
        List<Review> allReviews = reviewRepository.findByBook(book);

        if (allReviews.isEmpty()) return 20.0;

        java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
        long recentReviews = allReviews.stream()
                .filter(r -> r.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();

        // Puntuación basada en cantidad de reseñas recientes
        return Math.min(100.0, (recentReviews * 10.0));
    }

    /**
     * Calcula la puntuación por popularidad general (0-100)
     * Basada en número total de reseñas
     */
    private Double calculatePopularityScore(Book book) {
        List<Review> reviews = reviewRepository.findByBook(book);
        long reviewCount = reviews.size();

        // Puntuación basada en cantidad de reseñas (asumiendo que 100+ reseñas = 100 puntos)
        return Math.min(100.0, (reviewCount * 100.0 / 100.0));
    }

    /**
     * Genera razones de recomendación para mostrar al usuario
     */
    private List<String> generateRecommendationReasons(Book book, Long userId, Set<String> targetGenres) {
        List<String> reasons = new ArrayList<>();

        // Razón 1: Coincidencia de géneros
        Set<String> matchingGenres = book.getGenres().stream()
                .map(Genre::getName)
                .filter(targetGenres::contains)
                .collect(Collectors.toSet());

        if (!matchingGenres.isEmpty()) {
            reasons.add("Coincide con tu género preferido: " + String.join(", ", matchingGenres));
        }

        // Razón 2: Alta valoración
        List<Review> reviews = reviewRepository.findByBook(book);
        if (!reviews.isEmpty()) {
            double avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            if (avgRating >= 4.0) {
                reasons.add("Altamente valorado por otros usuarios (" + String.format("%.1f", avgRating) + "/5)");
            }
        }

        // Razón 3: Popular
        if (reviews.size() >= 10) {
            reasons.add("Muy popular en BookMatch (" + reviews.size() + " reseñas)");
        }

        if (reasons.isEmpty()) {
            reasons.add("Recomendado basado en tu actividad");
        }

        return reasons;
    }

    /**
     * Método auxiliar para crear RecommendationResponse
     */
    private RecommendationResponse createRecommendationResponse(Book book, Double score, List<String> reasons) {
        return RecommendationResponse.builder()
                .book(null) // Se llenará en el controlador con BookResponse enriquecida
                .score(score)
                .reasons(reasons != null ? reasons : new ArrayList<>())
                .build();
    }

    /**
     * LÓGICA DE RECOMENDACIÓN LEGACY (sin puntuación)
     * Se mantiene para compatibilidad hacia atrás
     */
    public List<Book> getPersonalizedRecommendations(Long userId) {
        // 1. Obtener el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Usamos un Set para evitar géneros duplicados
        Set<String> targetGenres = new HashSet<>();

        // A. Añadir géneros seleccionados en el perfil (Formulario inicial)
        if (user.getGenrePreferences() != null) {
            user.getGenrePreferences().forEach(pref ->
                    targetGenres.add(pref.getGenre().getName())
            );
        }

        // B. Añadir géneros de libros que el usuario ha valorado con 4 o 5 estrellas
        List<Review> goodReviews = reviewRepository.findByUser_UserIdAndRatingGreaterThanEqual(userId, 4);
        for (Review review : goodReviews) {
            review.getBook().getGenres().forEach(g -> targetGenres.add(g.getName()));
        }

        // 2. Si no sabemos nada del usuario (ni perfil ni reseñas), devolvemos aleatorios (Cold Start)
        if (targetGenres.isEmpty()) {
            return bookRepository.findRandomBooks(10);
        }

        // 3. Buscar libros en la BD que coincidan con esos géneros y NO haya leído
        List<String> genreList = new ArrayList<>(targetGenres);
        List<Book> recommendations = bookRepository.findRecommendations(genreList, userId);

        // Si la búsqueda personalizada devuelve muy pocos resultados, rellenamos con aleatorios
        if (recommendations.size() < 5) {
            List<Book> filler = bookRepository.findRandomBooks(5);
            // Añadimos solo los que no estén ya en la lista (evitar duplicados simples)
            for (Book b : filler) {
                if (!recommendations.contains(b)) {
                    recommendations.add(b);
                }
            }
        }

        // 4. Mezclar y limitar a 10 resultados
        Collections.shuffle(recommendations);
        return recommendations.stream().limit(10).toList();
    }
}