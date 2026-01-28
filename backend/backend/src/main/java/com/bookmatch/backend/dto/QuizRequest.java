package com.bookmatch.backend.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO para solicitudes de cuestionario de "Siguiente Lectura"
 * El usuario responde preguntas y obtiene una recomendación al azar que encaje
 */
@Data
public class QuizRequest {
    /** Respuestas a las preguntas del cuestionario */
    private List<QuizAnswer> answers;

    @Data
    public static class QuizAnswer {
        /** ID de la pregunta */
        private Integer questionId;
        /** ID de la opción de respuesta seleccionada */
        private Integer selectedOptionId;
    }
}
