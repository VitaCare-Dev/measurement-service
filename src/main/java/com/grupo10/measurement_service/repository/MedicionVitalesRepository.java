package com.grupo10.measurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo10.measurement_service.model.MedicionVitales;

import java.util.List;
import java.util.Optional;

public interface MedicionVitalesRepository extends JpaRepository<MedicionVitales, Long> {

    List<MedicionVitales> findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);

    Optional<MedicionVitales> findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);
}
