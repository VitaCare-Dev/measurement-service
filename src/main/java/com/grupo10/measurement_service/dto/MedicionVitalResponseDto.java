package com.grupo10.measurement_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de salida con los datos de una medición de signos vitales registrada.
 */
@Data
public class MedicionVitalResponseDto {

    private Long idControl;
    private Long idPaciente;
    private LocalDateTime fechaHora;
    private String notas;

    private Integer presionSistolica;
    private Integer presionDiastolica;
    private double temperatura;
    private double peso;
}