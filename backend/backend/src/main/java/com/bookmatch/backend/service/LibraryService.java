package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.BookStatusRequest;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.ReadingStatus;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.ReadingStatusRepository;
import com.bookmatch.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio para la gestión de la biblioteca personal del usuario.
 * Permite actualizar y mantener el estado de lectura de los libros en la estantería del usuario.
 */
@Service
public class LibraryService {

    @Autowired
    private ReadingStatusRepository readingStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    /**
     * Actualiza el estado de lectura de un libro para el usuario.
     * Si el libro no existe en base de datos, lo obtiene de Google Books.
     * Si ya existe una relación previa, actualiza el estado; si no, crea una nueva.
     *
     * @param request Datos del estado del libro (userId, googleBookId, status).
     * @return El estado de lectura guardado en base de datos.
     * @throws RuntimeException Si el usuario no se encuentra o el libro no es válido.
     */
    public ReadingStatus updateBookStatus(BookStatusRequest request) {
        // 1. Obtener el usuario
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Obtener (o crear) el libro
        Book book = bookService.findOrSaveBookFromGoogle(request.getGoogleBookId());
        if (book == null) {
            throw new RuntimeException("No se pudo encontrar el libro en Google");
        }

        // 3. Buscar si ya existe una relación (ej: estaba "Leyendo" y pasa a "Leído")
        Optional<ReadingStatus> existingStatus = readingStatusRepository.findByUserAndBook(user, book);

        ReadingStatus statusToSave;

        if (existingStatus.isPresent()) {
            // Actualizamos el existente
            statusToSave = existingStatus.get();
            statusToSave.setStatus(request.getStatus());
        } else {
            // Creamos uno nuevo
            statusToSave = ReadingStatus.builder()
                    .user(user)
                    .book(book)
                    .status(request.getStatus())
                    .build();
        }

        return readingStatusRepository.save(statusToSave);
    }
}