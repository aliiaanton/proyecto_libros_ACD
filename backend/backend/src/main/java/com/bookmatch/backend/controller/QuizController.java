package com.bookmatch.backend.controller;

import com.bookmatch.backend.dto.QuizRequest;
import com.bookmatch.backend.dto.QuizResponse;
import com.bookmatch.backend.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar el cuestionario de "Siguiente Lectura".
 * Proporciona un cuestionario que ayuda a los usuarios a encontrar su próximo libro
 * mediante un sistema de matching inteligente basado en sus respuestas.
 */
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    private QuizService quizService;

    /**
     * Obtiene las preguntas del cuestionario de "Siguiente Lectura".
     * Cada pregunta tiene múltiples opciones que el usuario puede seleccionar.
     *
     * @return Array de preguntas del cuestionario con sus opciones
     */
    @GetMapping("/questions")
    public ResponseEntity<?> getQuizQuestions() {
        logger.info("Obteniendo preguntas del cuestionario");

        @SuppressWarnings("unchecked")
        Map<String, Object>[] questions = new Map[]{
            Map.of(
                "id", 1,
                "question", "¿Cuál es tu género favorito?",
                "options", new String[]{"Fantasía", "Romance", "Misterio", "Ciencia Ficción", "Drama"}
            ),
            Map.of(
                "id", 2,
                "question", "¿Qué tipo de emociones buscas?",
                "options", new String[]{"Emocionante", "Conmovedor", "Intrigante", "Divertido", "Reflexivo"}
            ),
            Map.of(
                "id", 3,
                "question", "¿Cuánto tiempo tienes para leer?",
                "options", new String[]{"Menos de 200 págs", "200-400 págs", "400-600 págs", "Más de 600 págs"}
            ),
            Map.of(
                "id", 4,
                "question", "¿Prefieres final esperado o sorpresas?",
                "options", new String[]{"Final predecible", "Sorpresas", "Me da igual", "Giros inesperados"}
            )
        };
        return ResponseEntity.ok(questions);
    }

    /**
     * Procesa respuestas del cuestionario y devuelve un libro recomendado con matching.
     * Utiliza un algoritmo de scoring que considera género, duración, emociones y ratings.
     *
     * @param request Respuestas del usuario al cuestionario
     * @return QuizResponse con el libro recomendado, alternativas y porcentaje de match
     */
    @PostMapping("/answer")
    public ResponseEntity<?> submitQuizAnswers(@RequestBody QuizRequest request) {
        try {
            logger.info("Procesando respuestas del cuestionario - Total de respuestas: {}",
                request.getAnswers() != null ? request.getAnswers().size() : 0);

            QuizResponse response = quizService.processQuizAnswers(request);

            logger.info("Recomendación generada - Match: {}%", response.getMatchPercentage());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error procesando respuestas del quiz: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado en quiz", e);
            return ResponseEntity.badRequest().body("Error inesperado: " + e.getMessage());
        }
    }
}
