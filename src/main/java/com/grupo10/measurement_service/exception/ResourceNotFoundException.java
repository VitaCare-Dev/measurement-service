package com.grupo10.measurement_service.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la base de datos.
 * Resulta en una respuesta HTTP 404 Not Found al cliente.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
