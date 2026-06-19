package com.grupo10.measurement_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
@Table(name = "tb_control_salud")
public class ControlSalud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_control")
    private Long idControl;

    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "notas")
    private String notas;
}
