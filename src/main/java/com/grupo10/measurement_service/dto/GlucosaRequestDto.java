package com.grupo10.measurement_service.dto;

import lombok.Data;

@Data
public class GlucosaRequestDto {
    private Long idPaciente;
    private String notas;

    private int glucosa;
    private String periodo;

}
