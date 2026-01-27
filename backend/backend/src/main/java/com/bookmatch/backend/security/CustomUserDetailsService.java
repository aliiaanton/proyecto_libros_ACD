package com.bookmatch.backend.security;

import com.bookmatch.backend.entity.User;
import com.bookmatch.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscamos por EMAIL (ya que usas email para login normalmente)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Devolvemos un objeto User de Spring Security (no el nuestro de la Entity)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),          // Username (usamos el email)
                user.getPasswordHash(),   // Contraseña encriptada
                new ArrayList<>()         // Lista de roles (vacía por ahora o convertimos tu Enum)
        );
    }
}