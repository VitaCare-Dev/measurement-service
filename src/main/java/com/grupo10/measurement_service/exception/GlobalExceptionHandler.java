package com.grupo10.measurement_service.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.grupo10.measurement_service.dto.ErrorResponseDto;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateResource(DuplicateResourceException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessLogicException(BusinessLogicException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage(ex.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleMalformedJson(HttpMessageNotReadableException ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage("El cuerpo de la solicitud es inválido o está mal formado");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto();
        error.setMessage("Ocurrió un error interno en el servidor");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}