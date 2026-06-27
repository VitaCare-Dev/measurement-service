package com.grupo10.measurement_service.dto;

import com.grupo10.measurement_service.model.PeriodoGlucosa;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GlucosaResponseDto {
    private Long idControl;
    private Long idPaciente;
    private LocalDateTime fechaHora;
    private String notas;
    private Integer glucosa;
    private PeriodoGlucosa periodo;
}
