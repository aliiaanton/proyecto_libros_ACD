package com.bookmatch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para respuestas del cuestionario de "Siguiente Lectura".
 * Contiene el libro recomendado, explicación y alternativas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    /** El libro recomendado basado en las respuestas del cuestionario */
    private BookResponse recommendedBook;

    /** Explicación de por qué se recomienda este libro */
    private String explanation;

    /** Porcentaje de coincidencia entre las respuestas y el libro recomendado */
    private Double matchPercentage;

    /** Opciones alternativas (otros libros que también podrían gustar) */
    private List<BookResponse> alternativeBooks;
}
