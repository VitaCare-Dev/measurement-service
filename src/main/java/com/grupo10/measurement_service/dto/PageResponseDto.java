package com.grupo10.measurement_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Envoltorio de paginación devuelto por los endpoints de historial.
 * Se define explícitamente, en vez de serializar {@code Page<T>} de Spring
 * Data directamente, para no acoplar el contrato HTTP a los detalles
 * internos de Spring Data, que pueden cambiar entre versiones.
 */
@Data
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResponseDto(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
