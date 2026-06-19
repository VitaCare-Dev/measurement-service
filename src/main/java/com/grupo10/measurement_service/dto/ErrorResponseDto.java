package com.grupo10.measurement_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorResponseDto {
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
