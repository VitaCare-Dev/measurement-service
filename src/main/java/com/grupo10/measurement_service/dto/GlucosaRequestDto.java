package com.grupo10.measurement_service.dto;

import com.grupo10.measurement_service.model.PeriodoGlucosa;
import lombok.Data;

@Data
public class GlucosaRequestDto {
    private Long idPaciente;
    private String notas;

    private int glucosa;
    private PeriodoGlucosa periodo;

}
