package com.grupo10.measurement_service.dto;

import com.grupo10.measurement_service.model.PeriodoGlucosa;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO de salida con los datos de una medición de glucosa registrada.
 */
@Data
public class GlucosaResponseDto {
    private Long idControl;
    private Long idPaciente;
    private LocalDateTime fechaHora;
    private String notas;
    private Integer glucosa;
    private PeriodoGlucosa periodo;
}
