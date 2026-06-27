package com.grupo10.measurement_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de salida con los datos de un perfil lipídico registrado.
 */
@Data
public class LipidosResponseDto {
    private Long idControl;
    private Long idPaciente;
    private LocalDateTime fechaHora;
    private String notas;

    private int colesterolTotal;
    private int colesterolLDL;
    private int colesterolHDL;
    private int trigliceridos;
}