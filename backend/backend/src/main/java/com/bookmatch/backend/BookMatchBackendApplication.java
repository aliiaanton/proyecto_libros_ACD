package com.bookmatch.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

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

	/**
	 * Bean de configuración para RestTemplate.
	 * Permite realizar llamadas HTTP a APIs externas como Google Books.
	 *
	 * @return Instancia de RestTemplate configurada.
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}