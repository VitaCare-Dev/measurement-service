package com.grupo10.measurement_service.exception;

import com.grupo10.measurement_service.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_RetornaNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("No encontrado");
        ResponseEntity<ErrorResponseDto> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No encontrado", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleDuplicateResource_RetornaConflict() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicado");
        ResponseEntity<ErrorResponseDto> response = handler.handleDuplicateResource(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicado", response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleBusinessLogicException_RetornaBadRequest() {
        BusinessLogicException ex = new BusinessLogicException("Error de negocio");
        ResponseEntity<ErrorResponseDto> response = handler.handleBusinessLogicException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error de negocio", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleMalformedJson_RetornaBadRequest() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON inválido",
                new MockHttpInputMessage(new byte[]{}));
        ResponseEntity<ErrorResponseDto> response = handler.handleMalformedJson(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El cuerpo de la solicitud es inválido o está mal formado", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleGenericException_RetornaInternalServerError() {
        Exception ex = new Exception("Error inesperado");
        ResponseEntity<ErrorResponseDto> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocurrió un error interno en el servidor", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }
}
