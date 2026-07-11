package com.grupo10.measurement_service.model;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

/**
 * Entidad que representa una medición de signos vitales.
 * Almacena presión arterial sistólica y diastólica, temperatura corporal y peso.
 * Se relaciona con {@link ControlSalud} mediante una relación uno a uno,
 * compartiendo el mismo identificador como clave primaria.
 */
@Data
@Entity
@Table(name = "tb_medicion_vitales")
public class MedicionVitales {

    @Id
    @Column(name = "id_control")
    private Long idControl;

    @Column(name = "presion_sistolica")
    private Integer presionSistolica;

    @Column(name = "presion_diastolica")
    private Integer presionDiastolica;

    @Column(name = "temperatura")
    private double temperatura;

    @Column(name = "peso")
    private double peso;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_control")
    private ControlSalud controlSalud;
}
