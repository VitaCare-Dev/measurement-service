package com.grupo10.measurement_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de salida para las respuestas de error del microservicio.
 * Incluye el mensaje descriptivo del error, el código HTTP y la marca temporal del momento en que ocurrió.
 */
@Data
public class ErrorResponseDto {
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
