package com.grupo10.measurement_service.dto;

import lombok.Data;

/**
 * DTO de entrada para el registro de un perfil lipídico.
 */
@Data
public class LipidosRequestDto {
    private Long idPaciente;
    private String notas;

    private int colesterolTotal;
    private int colesterolLDL;
    private int colesterolHDL;
    private int trigliceridos;
}