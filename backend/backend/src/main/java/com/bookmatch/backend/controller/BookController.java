package com.bookmatch.backend.controller;

import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar operaciones relacionadas con libros.
 * Permite buscar libros utilizando la API de Google Books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

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
}