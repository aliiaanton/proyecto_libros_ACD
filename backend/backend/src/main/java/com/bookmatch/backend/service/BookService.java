package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.GoogleBooksResponse;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate; // Inyectamos el "teléfono"

    // URL base de la API de Google
    private final String GOOGLE_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    // Método para buscar libros en Google
    public List<Book> searchBooksInGoogle(String query) {
        // 1. Construimos la URL (ej: ...volumes?q=harry+potter)
        // Reemplazamos espacios por "+" porque las URLs no aceptan espacios
        String url = GOOGLE_API_URL + query.replace(" ", "+");

        // 2. Hacemos la llamada a Internet
        // Le decimos: "Ve a esta URL y lo que vuelva, mételo en la clase GoogleBooksResponse"
        GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

        List<Book> resultBooks = new ArrayList<>();

        // 3. Convertimos los datos de Google (DTO) a nuestros Libros (Entity)
        if (response != null && response.getItems() != null) {
            for (GoogleBooksResponse.Item item : response.getItems()) {
                Book book = convertToBookEntity(item);
                resultBooks.add(book);
            }
        }

        return resultBooks;
    }

    public Book getBookByGoogleId(String googleId) {
        return bookRepository.findByGoogleBookId(googleId).orElse(null);
    }

    // Método auxiliar para convertir de DTO a Entidad
    private Book convertToBookEntity(GoogleBooksResponse.Item item) {
        GoogleBooksResponse.VolumeInfo info = item.getVolumeInfo();

        // Manejo seguro de nulos (por si Google no manda autor o foto)
        String authors = (info.getAuthors() != null) ? String.join(", ", info.getAuthors()) : "Autor desconocido";
        String coverUrl = (info.getImageLinks() != null) ? info.getImageLinks().getThumbnail() : null;

        // Usamos el Builder que pusimos en la clase Book
        return Book.builder()
                .googleBookId(item.getId())
                .title(info.getTitle())
                .authors(authors)
                .description(info.getDescription()) // A veces viene muy largo, cuidado
                .publishedDate(info.getPublishedDate())
                .pageCount(info.getPageCount())
                .averageRatingApi(info.getAverageRating())
                .coverUrl(coverUrl)
                .build();
    }

    // URL para buscar un solo libro por ID
    private final String GOOGLE_API_SINGLE_URL = "https://www.googleapis.com/books/v1/volumes/";

    // Método PRINCIPAL: Busca en DB, si no está, va a Google y lo guarda
    public Book findOrSaveBookFromGoogle(String googleId) {
        // 1. ¿Lo tenemos ya en MySQL?
        return bookRepository.findByGoogleBookId(googleId)
                .orElseGet(() -> fetchAndSaveFromGoogle(googleId));
    }

    /**
     * Obtiene un libro de la API de Google Books y lo guarda en base de datos.
     * Método auxiliar privado utilizado por findOrSaveBookFromGoogle.
     *
     * @param googleId El ID del libro en Google Books.
     * @return El libro guardado en base de datos o null si no se encuentra.
     */
    private Book fetchAndSaveFromGoogle(String googleId) {
        String url = GOOGLE_API_SINGLE_URL + googleId;

        try {
            // Llamamos a Google pidiendo UN solo libro
            GoogleBooksResponse.Item item = restTemplate.getForObject(url, GoogleBooksResponse.Item.class);

            if (item != null) {
                Book newBook = convertToBookEntity(item); // Reusamos tu método convertidor existente
                return bookRepository.save(newBook); // ¡Aquí ocurre la magia de la persistencia!
            }
        } catch (Exception e) {
            System.err.println("Error trayendo libro de Google: " + e.getMessage());
        }
        return null; // O lanzar una excepción personalizada
    }
}