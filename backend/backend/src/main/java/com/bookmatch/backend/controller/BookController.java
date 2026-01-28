package com.bookmatch.backend.controller;

import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.Genre;
import com.bookmatch.backend.entity.Tag;
import com.bookmatch.backend.service.BookService;
import com.bookmatch.backend.repository.GenreRepository;
import com.bookmatch.backend.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar operaciones relacionadas con libros.
 * Permite buscar libros utilizando la API de Google Books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * Busca libros en la API de Google Books según el término de búsqueda.
     *
     * @param query Término de búsqueda (título, autor, ISBN, etc.).
     * @return Lista de libros encontrados.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String query) {
        List<Book> books = bookService.searchBooksInGoogle(query);
        return ResponseEntity.ok(books);
    }

    /**
     * Obtiene los géneros principales disponibles en la plataforma.
     *
     * @return Lista de géneros con conteo de libros
     */
    @GetMapping("/genres")
    public ResponseEntity<?> getGenres() {
        List<Genre> genres = genreRepository.findAll();
        return ResponseEntity.ok(genres.stream()
                .map(g -> new Object() {
                    public Long id = g.getGenreId();
                    public String name = g.getName();
                    public Long bookCount = (long) g.getBooks().size();
                })
                .collect(Collectors.toList()));
    }

    /**
     * Obtiene los tags principales disponibles en la plataforma.
     *
     * @return Lista de tags con conteo de libros
     */
    @GetMapping("/tags")
    public ResponseEntity<?> getTags() {
        List<Tag> tags = tagRepository.findAll();
        return ResponseEntity.ok(tags.stream()
                .map(t -> new Object() {
                    public Long id = t.getTagId();
                    public String name = t.getName();
                    public String description = t.getDescription();
                    public Long bookCount = (long) t.getBooks().size();
                })
                .collect(Collectors.toList()));
    }

    /**
     * Obtiene libros filtrados por un género específico.
     *
     * @param genreId ID del género a filtrar
     * @return Lista de libros del género
     */
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<?> getBooksByGenre(@PathVariable Long genreId) {
        Genre genre = genreRepository.findById(genreId)
                .orElse(null);

        if (genre == null) {
            return ResponseEntity.notFound().build();
        }

        List<Book> books = genre.getBooks().stream()
                .collect(Collectors.toList());

        return ResponseEntity.ok(books);
    }

    /**
     * Obtiene libros filtrados por una etiqueta específica.
     *
     * @param tagId ID de la etiqueta a filtrar
     * @return Lista de libros con la etiqueta
     */
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<?> getBooksByTag(@PathVariable Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElse(null);

        if (tag == null) {
            return ResponseEntity.notFound().build();
        }

        List<Book> books = tag.getBooks().stream()
                .collect(Collectors.toList());

        return ResponseEntity.ok(books);
    }

    /**
     * Obtiene un libro específico por su ID local.
     *
     * @param bookId ID del libro
     * @return Información del libro
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBook(@PathVariable Long bookId) {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    /**
     * Obtiene datos para la página principal de la aplicación.
     * Devuelve libros destacados, géneros y tags disponibles.
     *
     * @return ResponseEntity con datos de la página principal
     */
    @GetMapping("/home")
    public ResponseEntity<?> getHome() {
        try {
            List<Genre> genres = genreRepository.findAll().stream()
                    .limit(10)
                    .toList();

            List<Tag> tags = tagRepository.findAll().stream()
                    .limit(10)
                    .toList();

            List<Book> featuredBooks = bookService.findRandomBooks(6);

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("featuredBooks", featuredBooks);
            response.put("genres", genres);
            response.put("tags", tags);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}