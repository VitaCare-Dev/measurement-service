package com.grupo10.measurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo10.measurement_service.model.Lipidos;

import java.util.List;
import java.util.Optional;

public interface LipidosRepository extends JpaRepository<Lipidos, Long> {

    List<Lipidos> findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);

    Optional<Lipidos> findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);
}
