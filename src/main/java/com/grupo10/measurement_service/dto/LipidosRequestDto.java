package com.grupo10.measurement_service.dto;

import lombok.Data;

@Data
public class LipidosRequestDto {
    private Long idPaciente;
    private String notas;

    private int colesterolTotal;
    private int colesterolLDL;
    private int colesterolHDL;
    private int trigliceridos;
}