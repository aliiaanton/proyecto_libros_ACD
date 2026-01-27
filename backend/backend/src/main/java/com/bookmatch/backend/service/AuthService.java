package com.bookmatch.backend.service;

import com.bookmatch.backend.dto.AuthResponse;
import com.bookmatch.backend.dto.LoginRequest;
import com.bookmatch.backend.dto.RegisterRequest;
import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.enums.Role;
import com.bookmatch.backend.repository.UserRepository;
import com.bookmatch.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    /**
     * Autentica a un usuario existente verificando sus credenciales (email y contraseña).
     * Si las credenciales son válidas, genera y devuelve un token JWT para la sesión.
     *
     * @param request Objeto que contiene el email y la contraseña del usuario.
     * @return AuthResponse con el token JWT generado y el nombre de usuario.
     * @throws org.springframework.security.core.AuthenticationException Si las credenciales son incorrectas.
     * @throws java.util.NoSuchElementException Si el usuario no se encuentra en la base de datos tras la autenticación.
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

        // 3. Convertimos a UserDetails (objeto estándar de Spring Security) para generar el token
        // Usamos una lista vacía de autoridades por ahora
        var userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                new ArrayList<>()
        );

        // 4. Generar el Token JWT usando el servicio de seguridad
        String jwtToken = jwtService.generateToken(userDetails);

        // 5. Devolver la respuesta con el token
        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Verifica que el email no exista previamente, encripta la contraseña usando BCrypt
     * y asigna el rol de usuario base (USER) por defecto.
     *
     * @param request Objeto con los datos del formulario de registro (usuario, email, password).
     * @return El objeto User que ha sido guardado en la base de datos.
     * @throws RuntimeException Si el email ya está registrado en el sistema.
     */
    public User register(RegisterRequest request) {
        // 1. Validar que el usuario no exista ya
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Crear el usuario nuevo usando el patrón Builder
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword())) // ¡Aquí encriptamos la clave!
                .role(Role.USER) // Por defecto todos son usuarios normales
                .build();

        // 3. Guardar en MySQL
        return userRepository.save(user);
    }
}