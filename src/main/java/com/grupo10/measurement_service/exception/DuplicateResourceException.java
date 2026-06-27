package com.grupo10.measurement_service.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe.
 * Resulta en una respuesta HTTP 409 Conflict al cliente.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

}
