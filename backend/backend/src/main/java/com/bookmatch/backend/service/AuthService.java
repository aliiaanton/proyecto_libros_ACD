package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.AuthResponse;
import com.bookmatch.backend.dto.LoginRequest;
import com.bookmatch.backend.dto.RegisterRequest;
import com.bookmatch.backend.dto.UpdateUserProfileRequest;
import com.bookmatch.backend.entity.Genre;
import com.bookmatch.backend.entity.Tag;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.entity.UserGenrePreference;
import com.bookmatch.backend.entity.UserTagPreference;
import com.bookmatch.backend.enums.Role;
import com.bookmatch.backend.repository.GenreRepository;
import com.bookmatch.backend.repository.TagRepository;
import com.bookmatch.backend.repository.UserGenrePreferenceRepository;
import com.bookmatch.backend.repository.UserRepository;
import com.bookmatch.backend.repository.UserTagPreferenceRepository;
import com.bookmatch.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Servicio encargado de la lógica de autenticación y gestión de cuentas de usuario.
 * Maneja el registro de nuevos usuarios, la encriptación de contraseñas y
 * la generación de tokens JWT para el inicio de sesión.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserGenrePreferenceRepository userGenrePreferenceRepository;

    @Autowired
    private UserTagPreferenceRepository userTagPreferenceRepository;

    /**
     * Autentica a un usuario existente verificando sus credenciales (email y contraseña).
     * Si las credenciales son válidas, genera y devuelve un token JWT para la sesión.
     *
     * @param request Objeto que contiene el email y la contraseña del usuario.
     * @return AuthResponse con el token JWT generado y el nombre de usuario.
     * @throws org.springframework.security.core.AuthenticationException Si las credenciales son incorrectas.
     * @throws java.util.NoSuchElementException Si el usuario no se encuentra en la base de datos tras la autenticación.
     * @throws RuntimeException Si el email del usuario no ha sido verificado.
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar con Spring Security (esto comprueba usuario y contraseña automáticamente)
        // Si la contraseña es incorrecta, este método lanzará una excepción y no pasará a la siguiente línea.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Si pasa la autenticación, buscamos el usuario en la BD para obtener sus datos
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // 3. Verificar si el email está verificado
        if (!user.isEmailVerified()) {
            throw new RuntimeException("Debes verificar tu email antes de iniciar sesión");
        }

        // 4. Convertimos a UserDetails (objeto estándar de Spring Security) para generar el token
        // Usamos una lista vacía de autoridades por ahora
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                new ArrayList<>()
        );

        // 5. Generar el Token JWT usando el servicio de seguridad
        String jwtToken = jwtService.generateToken(userDetails);

        // 6. Devolver la respuesta con el token
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Verifica que el email no exista previamente, encripta la contraseña usando BCrypt
     * y asigna el rol de usuario base (USER) por defecto.
     * Genera un token de verificación y envía un email al usuario para que verifique su cuenta.
     * Guarda también las preferencias iniciales de géneros y tags si se proporcionan.
     *
     * @param request Objeto con los datos del formulario de registro (usuario, email, password, preferencias).
     * @return El objeto User que ha sido guardado en la base de datos.
     * @throws RuntimeException Si el email o username ya está registrado en el sistema.
     */
    public User register(RegisterRequest request) {
        // 1. Validar que el usuario no exista ya
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }

        // 2. Generar token de verificación
        String verificationToken = UUID.randomUUID().toString();

        // 3. Crear el usuario nuevo usando el patrón Builder
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // ¡Aquí encriptamos la clave!
                .role(Role.USER) // Por defecto todos son usuarios normales
                .emailVerified(false) // El email no está verificado inicialmente
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24)) // Token válido por 24 horas
                .build();

        // 4. Guardar en MySQL
        User savedUser = userRepository.save(user);

        // 5. Guardar preferencias de géneros si se proporcionan
        if (request.getGenrePreferenceIds() != null && !request.getGenrePreferenceIds().isEmpty()) {
            for (Long genreId : request.getGenrePreferenceIds()) {
                Genre genre = genreRepository.findById(genreId).orElse(null);
                if (genre != null) {
                    UserGenrePreference preference = UserGenrePreference.builder()
                            .user(savedUser)
                            .genre(genre)
                            .build();
                    userGenrePreferenceRepository.save(preference);
                }
            }
        }

        // 6. Guardar preferencias de tags si se proporcionan
        if (request.getTagPreferenceIds() != null && !request.getTagPreferenceIds().isEmpty()) {
            for (Long tagId : request.getTagPreferenceIds()) {
                Tag tag = tagRepository.findById(tagId).orElse(null);
                if (tag != null) {
                    UserTagPreference preference = UserTagPreference.builder()
                            .user(savedUser)
                            .tag(tag)
                            .build();
                    userTagPreferenceRepository.save(preference);
                }
            }
        }

        // 7. Enviar email de verificación
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        return savedUser;
    }

    /**
     * Verifica el email de un usuario usando el token de verificación.
     *
     * @param token Token de verificación enviado al email del usuario.
     * @throws RuntimeException Si el token es inválido o ha expirado.
     */
    public void verifyEmail(String token) {
        // 1. Buscar el usuario por el token
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        // 2. Verificar si el token ha expirado
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        // 3. Marcar el email como verificado y limpiar el token
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    /**
     * Actualiza el perfil del usuario (bio y preferencias).
     *
     * @param email Email del usuario a actualizar
     * @param request Datos a actualizar
     * @throws RuntimeException Si el usuario no se encuentra
     */
    public void updateUserProfile(String email, UpdateUserProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar bio si se proporciona
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        // Actualizar preferencias de géneros si se proporcionan
        if (request.getGenrePreferenceIds() != null) {
            // Eliminar preferencias anteriores
            userGenrePreferenceRepository.deleteAll(user.getGenrePreferences());

            // Añadir nuevas preferencias
            for (Long genreId : request.getGenrePreferenceIds()) {
                Genre genre = genreRepository.findById(genreId).orElse(null);
                if (genre != null) {
                    UserGenrePreference preference = UserGenrePreference.builder()
                            .user(user)
                            .genre(genre)
                            .build();
                    userGenrePreferenceRepository.save(preference);
                }
            }
        }

        // Actualizar preferencias de tags si se proporcionan
        if (request.getTagPreferenceIds() != null) {
            // Eliminar preferencias anteriores
            userTagPreferenceRepository.deleteAll(user.getTagPreferences());

            // Añadir nuevas preferencias
            for (Long tagId : request.getTagPreferenceIds()) {
                Tag tag = tagRepository.findById(tagId).orElse(null);
                if (tag != null) {
                    UserTagPreference preference = UserTagPreference.builder()
                            .user(user)
                            .tag(tag)
                            .build();
                    userTagPreferenceRepository.save(preference);
                }
            }
        }

        userRepository.save(user);
    }
}