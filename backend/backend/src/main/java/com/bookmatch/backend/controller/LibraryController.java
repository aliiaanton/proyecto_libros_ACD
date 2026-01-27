package com.bookmatch.backend.controller;

import com.bookmatch.backend.dto.BookStatusRequest;
import com.bookmatch.backend.entity.ReadingStatus;
import com.bookmatch.backend.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar la biblioteca personal del usuario.
 * Permite actualizar el estado de lectura de los libros en la estantería del usuario.
 */
@RestController
@RequestMapping("/api/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

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
}