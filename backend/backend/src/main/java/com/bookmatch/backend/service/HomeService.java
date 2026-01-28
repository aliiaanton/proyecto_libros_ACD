package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.HomeResponse;
import com.bookmatch.backend.entity.Book;
import com.bookmatch.backend.entity.Genre;
import com.bookmatch.backend.entity.Tag;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.BookRepository;
import com.bookmatch.backend.repository.GenreRepository;
import com.bookmatch.backend.repository.TagRepository;
import com.bookmatch.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la página principal (Home) de BookMatch.
 * Proporciona datos de libros destacados, géneros, tags y recomendaciones.
 */
@Service
public class HomeService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendationService recommendationService;

    /**
     * Obtiene datos para la página principal sin usuario autenticado.
     * Devuelve libros destacados, géneros y tags principales.
     *
     * @return HomeResponse con datos de la página principal pública
     */
    public HomeResponse getHomePagePublic() {
        List<HomeResponse.GenreDTO> genres = genreRepository.findAll().stream()
                .limit(10)
                .map(g -> HomeResponse.GenreDTO.builder()
                        .genreId(g.getGenreId())
                        .name(g.getName())
                        .bookCount((long) g.getBooks().size())
                        .build())
                .collect(Collectors.toList());

        List<HomeResponse.TagDTO> tags = tagRepository.findAll().stream()
                .limit(10)
                .map(t -> HomeResponse.TagDTO.builder()
                        .tagId(t.getTagId())
                        .name(t.getName())
                        .bookCount((long) t.getBooks().size())
                        .build())
                .collect(Collectors.toList());

        List<Book> featuredBooks = bookRepository.findRandomBooks(6);

        return HomeResponse.builder()
                .featuredBooks(featuredBooks.stream()
                        .map(this::convertToBookResponse)
                        .collect(Collectors.toList()))
                .personalRecommendations(null)
                .mainGenres(genres)
                .mainTags(tags)
                .build();
    }

    /**
     * Obtiene datos para la página principal de un usuario autenticado.
     * Incluye recomendaciones personalizadas además de libros destacados.
     *
     * @param userId ID del usuario autenticado
     * @return HomeResponse con datos personalizados
     */
    public HomeResponse getHomePageForUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            return getHomePagePublic();
        }

        List<HomeResponse.GenreDTO> genres = genreRepository.findAll().stream()
                .limit(10)
                .map(g -> HomeResponse.GenreDTO.builder()
                        .genreId(g.getGenreId())
                        .name(g.getName())
                        .bookCount((long) g.getBooks().size())
                        .build())
                .collect(Collectors.toList());

        List<HomeResponse.TagDTO> tags = tagRepository.findAll().stream()
                .limit(10)
                .map(t -> HomeResponse.TagDTO.builder()
                        .tagId(t.getTagId())
                        .name(t.getName())
                        .bookCount((long) t.getBooks().size())
                        .build())
                .collect(Collectors.toList());

        List<Book> featuredBooks = bookRepository.findRandomBooks(6);

        return HomeResponse.builder()
                .featuredBooks(featuredBooks.stream()
                        .map(this::convertToBookResponse)
                        .collect(Collectors.toList()))
                .personalRecommendations(null)
                .mainGenres(genres)
                .mainTags(tags)
                .build();
    }

    /**
     * Busca libros por término de búsqueda.
     * Busca en título, autores y descripción.
     *
     * @param query Término de búsqueda
     * @return Lista de libros que coinciden con la búsqueda
     */
    public List<Book> searchBooks(String query) {
        // Nota: Se recomienda implementar una query personalizada en BookRepository
        // Por ahora se devuelve todos los libros como placeholder
        return bookRepository.findAll();
    }

    /**
     * Obtiene libros por un género específico.
     *
     * @param genreId ID del género
     * @return Lista de libros del género
     */
    public List<Book> getBooksByGenre(Long genreId) {
        return genreRepository.findById(genreId)
                .map(g -> g.getBooks().stream()
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    /**
     * Obtiene libros por una etiqueta específica.
     *
     * @param tagId ID de la etiqueta
     * @return Lista de libros con la etiqueta
     */
    public List<Book> getBooksByTag(Long tagId) {
        return tagRepository.findById(tagId)
                .map(t -> t.getBooks().stream()
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    /**
     * Convierte una entidad Book a DTO BookResponse.
     *
     * @param book Entidad Book a convertir
     * @return BookResponse con los datos del libro
     */
    private com.bookmatch.backend.dto.BookResponse convertToBookResponse(Book book) {
        return com.bookmatch.backend.dto.BookResponse.builder()
                .bookId(book.getBookId())
                .googleBookId(book.getGoogleBookId())
                .title(book.getTitle())
                .authors(book.getAuthors())
                .description(book.getDescription())
                .isbn(book.getIsbn())
                .pageCount(book.getPageCount())
                .publishedDate(book.getPublishedDate())
                .coverUrl(book.getCoverUrl())
                .averageRatingApi(book.getAverageRatingApi())
                .genres(book.getGenres().stream()
                        .map(Genre::getName)
                        .collect(Collectors.toList()))
                .tags(book.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
