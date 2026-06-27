package com.grupo10.measurement_service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionClassesTest {

    @Test
    void businessLogicException_MensajeCorecto() {
        BusinessLogicException ex = new BusinessLogicException("error de negocio");
        assertEquals("error de negocio", ex.getMessage());
    }

    @Test
    void resourceNotFoundException_MensajeCorecto() {
        ResourceNotFoundException ex = new ResourceNotFoundException("no encontrado");
        assertEquals("no encontrado", ex.getMessage());
    }

    @Test
    void duplicateResourceException_MensajeCorecto() {
        DuplicateResourceException ex = new DuplicateResourceException("recurso duplicado");
        assertEquals("recurso duplicado", ex.getMessage());
    }
}
