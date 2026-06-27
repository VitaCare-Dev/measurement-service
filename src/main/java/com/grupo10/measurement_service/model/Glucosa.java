package com.grupo10.measurement_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Entidad que representa una medición de glucosa.
 * Se relaciona con {@link ControlSalud} mediante una relación uno a uno,
 * compartiendo el mismo identificador como clave primaria.
 */
@Data
@Entity
@Table(name = "tb_medicion_glucosa")
public class Glucosa {

    @Id
    @Column(name = "id_control")
    private Long idControl;

    @Column(name = "glucosa")
    private int nivelGlucosa;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo")
    private PeriodoGlucosa periodo;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_control")
    private ControlSalud controlSalud;
}
