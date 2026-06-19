package com.grupo10.measurement_service.dto;

import lombok.Data;

@Data
public class MedicionVitalRequestDto {

    private Long idPaciente;
    private String notas;

    private Integer presionSistolica;
    private Integer presionDiastolica;
    private double temperatura;
    private double peso;
}