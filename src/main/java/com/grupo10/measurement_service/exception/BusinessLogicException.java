package com.grupo10.measurement_service.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 * Por ejemplo: valores de medición fuera de rango, campos obligatorios ausentes
 * o datos que no cumplen las restricciones del dominio médico.
 */
public class BusinessLogicException extends RuntimeException {
    public BusinessLogicException(String message) {
        super(message);
    }
}