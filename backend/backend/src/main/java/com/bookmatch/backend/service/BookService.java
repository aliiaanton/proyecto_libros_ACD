package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.GoogleBooksResponse;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate; // Inyectamos el "teléfono"

    // URL base de la API de Google
    private final String GOOGLE_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    /**
     * Busca libros en Google Books con manejo de errores y timeouts.
     * @param query Término de búsqueda
     * @return Lista de libros encontrados o lista vacía si hay error
     */
    public List<Book> searchBooksInGoogle(String query) {
        if (query == null || query.trim().isEmpty()) {
            logger.warn("Búsqueda realizada con query vacío");
            return new ArrayList<>();
        }

        // 1. Construimos la URL (ej: ...volumes?q=harry+potter)
        // Reemplazamos espacios por "+" porque las URLs no aceptan espacios
        String url = GOOGLE_API_URL + query.replace(" ", "+");
        logger.info("Buscando libros con URL: {}", url);

        try {
            // 2. Hacemos la llamada a Internet con timeout configurado
            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);

            List<Book> resultBooks = new ArrayList<>();

            // 3. Convertimos los datos de Google (DTO) a nuestros Libros (Entity)
            if (response != null && response.getItems() != null) {
                logger.info("Se encontraron {} libros para la búsqueda: {}", response.getItems().size(), query);
                for (GoogleBooksResponse.Item item : response.getItems()) {
                    Book book = convertToBookEntity(item);
                    resultBooks.add(book);
                }
            } else {
                logger.info("No se encontraron libros para la búsqueda: {}", query);
            }

            return resultBooks;

        } catch (ResourceAccessException e) {
            logger.error("Timeout o error de conexión al buscar en Google Books: {}", e.getMessage());
            return new ArrayList<>();
        } catch (HttpClientErrorException e) {
            logger.error("Error HTTP al buscar en Google Books: {} - {}", e.getStatusCode(), e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error inesperado al buscar en Google Books: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    public Book getBookByGoogleId(String googleId) {
        return bookRepository.findByGoogleBookId(googleId).orElse(null);
    }

    /**
     * Obtiene un libro por su ID local en la base de datos.
     *
     * @param bookId ID del libro en la base de datos local.
     * @return El libro encontrado o null si no existe.
     */
    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }

    /**
     * Obtiene una lista de libros aleatorios de la base de datos.
     *
     * @param limit Número de libros aleatorios a obtener.
     * @return Lista de libros aleatorios.
     */
    public List<Book> findRandomBooks(int limit) {
        return bookRepository.findRandomBooks(limit);
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
            logger.info("Obteniendo libro de Google Books con ID: {}", googleId);
            // Llamamos a Google pidiendo UN solo libro
            GoogleBooksResponse.Item item = restTemplate.getForObject(url, GoogleBooksResponse.Item.class);

            if (item != null) {
                logger.info("Libro encontrado en Google Books: {}", googleId);
                Book newBook = convertToBookEntity(item); // Reusamos tu método convertidor existente
                return bookRepository.save(newBook); // ¡Aquí ocurre la magia de la persistencia!
            } else {
                logger.warn("No se encontró libro en Google Books con ID: {}", googleId);
            }
        } catch (ResourceAccessException e) {
            logger.error("Timeout al obtener libro {} de Google Books: {}", googleId, e.getMessage());
        } catch (HttpClientErrorException e) {
            logger.error("Error HTTP {} al obtener libro {}: {}", e.getStatusCode(), googleId, e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al obtener libro {} de Google Books: {}", googleId, e.getMessage(), e);
        }
        return null; // O lanzar una excepción personalizada
    }
}