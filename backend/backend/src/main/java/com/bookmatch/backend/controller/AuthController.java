package com.bookmatch.backend.controller;

import com.bookmatch.backend.dto.AuthResponse;
import com.bookmatch.backend.dto.LoginRequest;
import com.bookmatch.backend.dto.RegisterRequest;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar la autenticación y registro de usuarios.
 * Proporciona endpoints para el registro de nuevos usuarios en la aplicación BookMatch.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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
            return ResponseEntity.ok("Usuario registrado con éxito: " + newUser.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}