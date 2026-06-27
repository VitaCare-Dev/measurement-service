package com.grupo10.measurement_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio de mediciones médicas.
 * Gestiona el registro de glucosa, lípidos y signos vitales de pacientes.
 */
@SpringBootApplication
public class MeasurementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeasurementServiceApplication.class, args);
	}

}
