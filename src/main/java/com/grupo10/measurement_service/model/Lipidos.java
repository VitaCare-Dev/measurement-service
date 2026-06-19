package com.grupo10.measurement_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Data;
import jakarta.persistence.Table;

@Data
@Entity
@Table(name = "tb_medicion_lipidemia")
public class Lipidos {

    @Id
    @Column(name = "id_control")
    private Long idControl;

    @Column(name = "colesterol_total")
    private int colesterolTotal;

    @Column(name = "colesterol_ldl")
    private int colesterolLDL;

    @Column(name = "colesterol_hdl")
    private int colesterolHDL;

    @Column(name = "trigliceridos")
    private int trigliceridos;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_control")
    private ControlSalud controlSalud;
}
