package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.BookResponse;
import com.bookmatch.backend.dto.QuizRequest;
import com.bookmatch.backend.dto.QuizResponse;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.Genre;
import com.bookmatch.backend.entity.Tag;
import com.bookmatch.backend.repository.BookRepository;
import com.bookmatch.backend.repository.GenreRepository;
import com.bookmatch.backend.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la lógica del cuestionario de "Siguiente Lectura".
 * Implementa un matching inteligente entre respuestas del usuario y libros.
 */
@Service
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * Procesa las respuestas del cuestionario y retorna un libro recomendado con matching.
     * Analiza todas las respuestas y busca un libro que se alinee con los criterios del usuario.
     *
     * @param request Objeto con las respuestas del usuario al cuestionario
     * @return QuizResponse con el libro recomendado, alternativas y porcentaje de match
     */
    public QuizResponse processQuizAnswers(QuizRequest request) {
        logger.info("Procesando respuestas del cuestionario");

        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new RuntimeException("Se requieren respuestas al cuestionario");
        }

        // Extraer información de las respuestas
        QuizAnalysis analysis = analyzeAnswers(request.getAnswers());
        logger.info("Análisis del quiz - Género: {}, Emociones: {}, Duración: {}",
            analysis.preferredGenre, analysis.preferredEmotion, analysis.maxPageCount);

        // Obtener todos los libros disponibles
        List<Book> allBooks = bookRepository.findAll();

        if (allBooks.isEmpty()) {
            throw new RuntimeException("No hay libros disponibles en la base de datos");
        }

        // Calcular score para cada libro basado en el análisis
        List<BookScore> scoredBooks = allBooks.stream()
                .map(book -> calculateBookScore(book, analysis))
                .filter(bs -> bs.score > 0) // Solo libros con alguna coincidencia
                .sorted(Comparator.comparingDouble(BookScore::getScore).reversed())
                .collect(Collectors.toList());

        if (scoredBooks.isEmpty()) {
            // Fallback: retornar libros al azar si no hay coincidencias exactas
            scoredBooks = allBooks.stream()
                    .map(book -> new BookScore(book, 50.0))
                    .limit(5)
                    .collect(Collectors.toList());
        }

        // El libro con mayor score es la recomendación principal
        BookScore mainRecommendation = scoredBooks.get(0);
        Book recommendedBook = mainRecommendation.book;

        // Obtener alternativas (siguientes 3 libros con mejor score)
        List<BookResponse> alternativeBooks = scoredBooks.stream()
                .skip(1)
                .limit(3)
                .map(bs -> convertToBookResponse(bs.book))
                .collect(Collectors.toList());

        // Generar explicación personalizada
        String explanation = generateExplanation(recommendedBook, analysis);

        return QuizResponse.builder()
                .recommendedBook(convertToBookResponse(recommendedBook))
                .explanation(explanation)
                .matchPercentage(mainRecommendation.score)
                .alternativeBooks(alternativeBooks)
                .build();
    }

    /**
     * Analiza las respuestas del cuestionario para extraer preferencias del usuario.
     */
    private QuizAnalysis analyzeAnswers(List<QuizRequest.QuizAnswer> answers) {
        QuizAnalysis analysis = new QuizAnalysis();

        Map<Integer, Integer> questionAnswers = new HashMap<>();
        for (QuizRequest.QuizAnswer answer : answers) {
            questionAnswers.put(answer.getQuestionId(), answer.getSelectedOptionId());
        }

        // Pregunta 1: ¿Cuál es tu género favorito?
        // Opciones: 0=Fantasía, 1=Romance, 2=Misterio, 3=Ciencia Ficción, 4=Drama
        Integer genreAnswer = questionAnswers.get(1);
        if (genreAnswer != null) {
            switch (genreAnswer) {
                case 0 -> analysis.preferredGenre = "Fantasía";
                case 1 -> analysis.preferredGenre = "Romance";
                case 2 -> analysis.preferredGenre = "Misterio";
                case 3 -> analysis.preferredGenre = "Ciencia Ficción";
                case 4 -> analysis.preferredGenre = "Drama";
            }
        }

        // Pregunta 2: ¿Qué tipo de emociones buscas?
        // Opciones: 0=Emocionante, 1=Conmovedor, 2=Intrigante, 3=Divertido, 4=Reflexivo
        Integer emotionAnswer = questionAnswers.get(2);
        if (emotionAnswer != null) {
            switch (emotionAnswer) {
                case 0 -> analysis.preferredEmotion = "Emocionante";
                case 1 -> analysis.preferredEmotion = "Conmovedor";
                case 2 -> analysis.preferredEmotion = "Intrigante";
                case 3 -> analysis.preferredEmotion = "Divertido";
                case 4 -> analysis.preferredEmotion = "Reflexivo";
            }
        }

        // Pregunta 3: ¿Cuánto tiempo para leer?
        // Opciones: 0=Menos de 200, 1=200-400, 2=400-600, 3=Más de 600
        Integer lengthAnswer = questionAnswers.get(3);
        if (lengthAnswer != null) {
            switch (lengthAnswer) {
                case 0 -> analysis.maxPageCount = 200;
                case 1 -> analysis.maxPageCount = 400;
                case 2 -> analysis.maxPageCount = 600;
                case 3 -> analysis.maxPageCount = Integer.MAX_VALUE;
            }
        }

        // Pregunta 4: ¿Prefieres final esperado o sorpresas?
        // Opciones: 0=Predecible, 1=Sorpresas, 2=Me da igual, 3=Giros inesperados
        Integer endingAnswer = questionAnswers.get(4);
        if (endingAnswer != null) {
            analysis.prefersUnexpectedEnding = (endingAnswer == 1 || endingAnswer == 3);
        }

        return analysis;
    }

    /**
     * Calcula una puntuación (0-100) para un libro basado en el análisis del quiz.
     * Considera múltiples factores: género, duración, emociones, rating, etc.
     */
    private BookScore calculateBookScore(Book book, QuizAnalysis analysis) {
        double score = 0.0;
        double maxScore = 0.0;

        // 1. Matching de género (peso: 40%)
        maxScore += 40;
        if (analysis.preferredGenre != null) {
            boolean genreMatch = book.getGenres().stream()
                    .anyMatch(g -> g.getName().equalsIgnoreCase(analysis.preferredGenre));
            if (genreMatch) {
                score += 40;
            } else {
                // Dar puntos parciales si el libro tiene al menos un género (diversidad)
                if (!book.getGenres().isEmpty()) {
                    score += 15;
                }
            }
        } else {
            score += 20; // Si no especificó género, dar puntos por tener géneros
        }

        // 2. Duración del libro (peso: 30%)
        maxScore += 30;
        if (book.getPageCount() != null && book.getPageCount() > 0) {
            int pageCount = book.getPageCount();
            if (pageCount <= analysis.maxPageCount) {
                // Mejor puntuación para libros más cercanos al máximo deseado
                score += 30 * (1.0 - Math.abs(pageCount - (analysis.maxPageCount / 2.0)) / (analysis.maxPageCount / 2.0));
                score = Math.max(score, 20); // Mínimo 20 si está dentro del rango
            }
        } else {
            score += 15; // Si no sabemos las páginas, dar crédito
        }

        // 3. Valoración promedio del libro (peso: 20%)
        maxScore += 20;
        if (book.getAverageRatingApi() != null && book.getAverageRatingApi() > 0) {
            // Normalizar el rating (0-5) a puntos (0-20)
            score += (book.getAverageRatingApi() / 5.0) * 20;
        } else {
            score += 10; // Si no tiene rating, dar puntos por defecto
        }

        // 4. Emociones/Tags (peso: 10%)
        maxScore += 10;
        if (analysis.preferredEmotion != null) {
            boolean emotionMatch = book.getTags().stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase(analysis.preferredEmotion));
            if (emotionMatch) {
                score += 10;
            } else {
                score += 3; // Puntos parciales
            }
        } else {
            score += 5;
        }

        // Normalizar score a 0-100
        double finalScore = (score / Math.max(maxScore, 1.0)) * 100;
        return new BookScore(book, Math.min(finalScore, 100.0));
    }

    /**
     * Genera una explicación personalizada de por qué se recomienda este libro.
     */
    private String generateExplanation(Book book, QuizAnalysis analysis) {
        List<String> reasons = new ArrayList<>();

        if (analysis.preferredGenre != null) {
            boolean hasGenre = book.getGenres().stream()
                    .anyMatch(g -> g.getName().equalsIgnoreCase(analysis.preferredGenre));
            if (hasGenre) {
                reasons.add("coincide con tu género preferido " + analysis.preferredGenre);
            }
        }

        if (book.getPageCount() != null && book.getPageCount() > 0) {
            reasons.add("tiene " + book.getPageCount() + " páginas (dentro de tu preferencia)");
        }

        if (book.getAverageRatingApi() != null && book.getAverageRatingApi() >= 4) {
            reasons.add("tiene muy buenas valoraciones (" + String.format("%.1f", book.getAverageRatingApi()) + "/5)");
        }

        if (analysis.preferredEmotion != null) {
            boolean hasTag = book.getTags().stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase(analysis.preferredEmotion));
            if (hasTag) {
                reasons.add("es " + analysis.preferredEmotion.toLowerCase());
            }
        }

        if (reasons.isEmpty()) {
            return "Se recomienda este libro basado en tu perfil de lectura.";
        }

        return "Te recomendamos este libro porque " + String.join(", ", reasons) + ".";
    }

    /**
     * Convierte una entidad Book a BookResponse DTO.
     */
    private BookResponse convertToBookResponse(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .googleBookId(book.getGoogleBookId())
                .title(book.getTitle())
                .authors(book.getAuthors())
                .description(book.getDescription())
                .publishedDate(book.getPublishedDate())
                .pageCount(book.getPageCount())
                .coverUrl(book.getCoverUrl())
                .averageRatingApi(book.getAverageRatingApi())
                .build();
    }

    /**
     * Clase interna para almacenar análisis de respuestas del quiz.
     */
    private static class QuizAnalysis {
        String preferredGenre;
        String preferredEmotion;
        int maxPageCount = 600; // Por defecto
        boolean prefersUnexpectedEnding = false;
    }

    /**
     * Clase interna para asociar libro con su score.
     */
    private static class BookScore {
        Book book;
        Double score;

        BookScore(Book book, Double score) {
            this.book = book;
            this.score = score;
        }

        public Double getScore() {
            return score;
        }
    }
}
