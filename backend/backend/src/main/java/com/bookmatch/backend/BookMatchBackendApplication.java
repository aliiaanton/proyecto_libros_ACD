package com.bookmatch.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Clase principal de la aplicación BookMatch Backend.
 * Punto de entrada de la aplicación Spring Boot.
 */
@SpringBootApplication
@EnableAsync
public class BookMatchBackendApplication {

	/**
	 * Método principal que inicia la aplicación Spring Boot.
	 *
	 * @param args Argumentos de línea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BookMatchBackendApplication.class, args);
	}
}