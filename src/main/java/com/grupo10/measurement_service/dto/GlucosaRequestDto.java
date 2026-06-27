package com.grupo10.measurement_service.dto;

import com.grupo10.measurement_service.model.PeriodoGlucosa;
import lombok.Data;

/**
 * DTO de entrada para el registro de una medición de glucosa.
 */
@Data
public class GlucosaRequestDto {
    private Long idPaciente;
    private String notas;

    private int glucosa;
    private PeriodoGlucosa periodo;

}
