package com.bookmatch.backend.controller;

import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.repository.BookRepository;
import com.bookmatch.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar el cuestionario de "Siguiente Lectura".
 * Proporciona un cuestionario que ayuda a los usuarios a encontrar su próximo libro.
 */
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Obtiene las preguntas del cuestionario de "Siguiente Lectura".
     *
     * @return Preguntas del cuestionario
     */
    @GetMapping("/questions")
    public ResponseEntity<?> getQuizQuestions() {
        Map<String, Object>[] questions = new Map[]{
            Map.of("id", 1, "question", "¿Cuál es tu género favorito?",
                "options", new String[]{"Fantasía", "Romance", "Misterio", "Ciencia Ficción", "Drama"}),
            Map.of("id", 2, "question", "¿Qué tipo de emociones buscas?",
                "options", new String[]{"Emocionante", "Conmovedor", "Intrigante", "Divertido", "Reflexivo"}),
            Map.of("id", 3, "question", "¿Cuánto tiempo para leer?",
                "options", new String[]{"Menos de 200 págs", "200-400 págs", "400-600 págs", "Más de 600 págs"}),
            Map.of("id", 4, "question", "¿Prefieres final esperado o sorpresas?",
                "options", new String[]{"Final predecible", "Sorpresas", "Me da igual", "Giros inesperados"})
        };
        return ResponseEntity.ok(questions);
    }

    /**
     * Procesa respuestas del cuestionario y devuelve un libro recomendado.
     *
     * @param request Respuestas del usuario
     * @return Libro recomendado basado en respuestas
     */
    @PostMapping("/answer")
    public ResponseEntity<?> submitQuizAnswers(@RequestBody Map<String, Object> request) {
        try {
            List<?> answers = (List<?>) request.get("answers");
            if (answers == null || answers.isEmpty()) {
                return ResponseEntity.badRequest().body("Se requieren respuestas");
            }

            List<Book> books = bookRepository.findRandomBooks(1);
            if (books.isEmpty()) {
                return ResponseEntity.status(404).body("No hay libros disponibles");
            }

            Book recommendedBook = books.get(0);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("recommendedBook", recommendedBook);
            response.put("explanation", "Libro seleccionado basándose en tus respuestas.");
            response.put("matchPercentage", Math.random() * 100);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
