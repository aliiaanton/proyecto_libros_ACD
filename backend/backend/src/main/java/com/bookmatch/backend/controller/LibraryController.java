package com.bookmatch.backend.controller;

import com.bookmatch.backend.dto.BookStatusRequest;
import com.bookmatch.backend.entity.ReadingStatus;
import com.bookmatch.backend.entity.CustomList;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.UserRepository;
import com.bookmatch.backend.repository.CustomListRepository;
import com.bookmatch.backend.repository.BookRepository;
import com.bookmatch.backend.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

/**
 * Controlador REST para gestionar la biblioteca personal del usuario.
 * Permite actualizar el estado de lectura de los libros en la estantería del usuario.
 */
@RestController
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomListRepository customListRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Actualiza el estado de lectura de un libro para el usuario.
     *
     * @param request Datos del estado del libro (userId, googleBookId, status).
     * @return ResponseEntity con el estado guardado o mensaje de error.
     */
    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody BookStatusRequest request) {
        try {
            ReadingStatus savedStatus = libraryService.updateBookStatus(request);
            return ResponseEntity.ok("Libro guardado en estantería: " + savedStatus.getStatus());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crea una nueva lista personalizada para el usuario autenticado.
     *
     * @param request Datos de la lista (nombre, descripción, isPublic)
     * @return ResponseEntity con la lista creada
     */
    @PostMapping("/custom-list")
    public ResponseEntity<?> createCustomList(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            CustomList list = CustomList.builder()
                    .user(user)
                    .name((String) request.get("name"))
                    .description((String) request.getOrDefault("description", ""))
                    .isPublic((Boolean) request.getOrDefault("isPublic", true))
                    .createdAt(LocalDateTime.now())
                    .build();

            CustomList saved = customListRepository.save(list);
            return ResponseEntity.ok("Lista creada: " + saved.getListId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtiene todas las listas personalizadas del usuario autenticado.
     *
     * @return Lista de listas personalizadas del usuario
     */
    @GetMapping("/custom-lists")
    public ResponseEntity<?> getUserCustomLists() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<CustomList> lists = customListRepository.findByUser(user);
            return ResponseEntity.ok(lists);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Añade un libro a una lista personalizada.
     *
     * @param listId ID de la lista
     * @param bookId ID del libro
     * @return ResponseEntity con mensaje de éxito
     */
    @PostMapping("/custom-list/{listId}/add-book")
    public ResponseEntity<?> addBookToList(@PathVariable Long listId, @RequestParam Long bookId) {
        try {
            CustomList list = customListRepository.findById(listId)
                    .orElseThrow(() -> new RuntimeException("Lista no encontrada"));

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

            list.getBooks().add(book);
            customListRepository.save(list);

            return ResponseEntity.ok("Libro añadido a la lista");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Elimina un libro de una lista personalizada.
     *
     * @param listId ID de la lista
     * @param bookId ID del libro
     * @return ResponseEntity con mensaje de éxito
     */
    @DeleteMapping("/custom-list/{listId}/remove-book")
    public ResponseEntity<?> removeBookFromList(@PathVariable Long listId, @RequestParam Long bookId) {
        try {
            CustomList list = customListRepository.findById(listId)
                    .orElseThrow(() -> new RuntimeException("Lista no encontrada"));

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

            list.getBooks().remove(book);
            customListRepository.save(list);

            return ResponseEntity.ok("Libro eliminado de la lista");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Elimina una lista personalizada.
     *
     * @param listId ID de la lista a eliminar
     * @return ResponseEntity con mensaje de éxito
     */
    @DeleteMapping("/custom-list/{listId}")
    public ResponseEntity<?> deleteCustomList(@PathVariable Long listId) {
        try {
            customListRepository.deleteById(listId);
            return ResponseEntity.ok("Lista eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}