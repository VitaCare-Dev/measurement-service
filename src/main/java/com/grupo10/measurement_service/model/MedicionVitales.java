package com.grupo10.measurement_service.model;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Data
@Entity
@Table(name = "tb_medicion_vitales")
public class MedicionVitales {

    @Id
    @Column(name = "id_control")
    private Long idControl;

    @Column(name = "presion_sistolica")
    private int presionSistolica;

    @Column(name = "presion_diastolica")
    private int presionDiastolica;

    @Column(name = "temperatura")
    private double temperatura;

    @Column(name = "peso")
    private double peso;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_control")
    private ControlSalud controlSalud;
}
