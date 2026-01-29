package com.bookmatch.backend.controller;

import com.bookmatch.backend.dto.AuthResponse;
import com.bookmatch.backend.dto.LoginRequest;
import com.bookmatch.backend.dto.RegisterRequest;
import com.bookmatch.backend.dto.UpdateUserProfileRequest;
import com.bookmatch.backend.entity.Genre;
import com.bookmatch.backend.entity.Tag;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.GenreRepository;
import com.bookmatch.backend.repository.TagRepository;
import com.bookmatch.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestionar la autenticación y registro de usuarios.
 * Proporciona endpoints para el registro de nuevos usuarios en la aplicación BookMatch.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Datos de registro del usuario (username, email, password).
     * @return ResponseEntity con mensaje de éxito o error.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.register(request);
            return ResponseEntity.ok("Usuario registrado con éxito. Por favor, verifica tu email para activar tu cuenta.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint de inicio de sesión.
     *
     * @param request Credenciales del usuario (email y contraseña).
     * @return ResponseEntity con el token JWT y datos del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Verifica el email de un usuario usando el token enviado por correo.
     *
     * @param token Token de verificación único.
     * @return ResponseEntity con mensaje de éxito o error.
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("Email verificado correctamente. Ya puedes iniciar sesión.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtiene los géneros y tags disponibles para el formulario de preferencias.
     * Usado en el formulario de registro e en la sección de edición de perfil.
     *
     * @return ResponseEntity con listas de géneros y tags disponibles
     */
    @GetMapping("/preferences-options")
    public ResponseEntity<?> getPreferencesOptions() {
        try {
            List<Genre> genres = genreRepository.findAll();
            List<Tag> tags = tagRepository.findAll();

            List<Map<String, Object>> genreOptions = genres.stream()
                    .map(g -> {
                        Map<String, Object> gMap = new HashMap<>();
                        gMap.put("id", g.getGenreId());
                        gMap.put("name", g.getName());
                        return gMap;
                    })
                    .collect(Collectors.toList());

            List<Map<String, Object>> tagOptions = tags.stream()
                    .map(t -> {
                        Map<String, Object> tMap = new HashMap<>();
                        tMap.put("id", t.getTagId());
                        tMap.put("name", t.getName());
                        tMap.put("description", t.getDescription());
                        return tMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("genres", genreOptions);
            response.put("tags", tagOptions);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     * Permite cambiar bio y preferencias de géneros/tags.
     *
     * @param request Datos a actualizar
     * @return ResponseEntity con mensaje de éxito o error
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateUserProfileRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            authService.updateUserProfile(email, request);
            return ResponseEntity.ok("Perfil actualizado correctamente.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}