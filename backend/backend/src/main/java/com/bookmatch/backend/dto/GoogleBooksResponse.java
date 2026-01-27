package com.bookmatch.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * DTO (Data Transfer Object) para mapear las respuestas de la API de Google Books.
 * Estructura principal que contiene una lista de libros (items) devueltos por Google.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksResponse {
    /** Lista de libros devueltos por la API de Google Books */
    private List<Item> items;

    /**
     * Clase interna que representa un item individual (un libro) en la respuesta de Google Books.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        /** ID único del libro en Google Books */
        private String id;

        /** Información detallada del volumen/libro */
        private VolumeInfo volumeInfo;
    }

    /**
     * Clase interna que contiene la información detallada de un libro.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VolumeInfo {
        /** Título del libro */
        private String title;

        /** Lista de autores del libro */
        private List<String> authors;

        /** Descripción o sinopsis del libro */
        private String description;

        /** Fecha de publicación del libro */
        private String publishedDate;

        /** Número de páginas del libro */
        private Integer pageCount;

        /** Categorías o géneros asignados por Google */
        private List<String> categories;

        /** Enlaces a las imágenes de portada */
        private ImageLinks imageLinks;

        /** Calificación promedio según Google Books */
        private Double averageRating;
    }

    /**
     * Clase interna que contiene los enlaces a las imágenes de portada del libro.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageLinks {
        /** URL de la imagen de portada en tamaño thumbnail */
        private String thumbnail;

        /** URL de la imagen de portada en tamaño pequeño */
        private String smallThumbnail;
    }
}