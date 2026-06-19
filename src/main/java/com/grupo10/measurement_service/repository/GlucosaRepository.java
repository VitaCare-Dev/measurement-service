package com.grupo10.measurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo10.measurement_service.model.Glucosa;

import java.util.List;
import java.util.Optional;

public interface GlucosaRepository extends JpaRepository<Glucosa, Long> {

    List<Glucosa> findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);

    Optional<Glucosa> findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);
}
