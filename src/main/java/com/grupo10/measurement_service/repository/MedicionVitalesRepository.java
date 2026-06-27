package com.grupo10.measurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo10.measurement_service.model.MedicionVitales;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link MedicionVitales}.
 * Provee operaciones CRUD estándar y consultas derivadas por paciente.
 */
public interface MedicionVitalesRepository extends JpaRepository<MedicionVitales, Long> {

    /**
     * Obtiene todas las mediciones de signos vitales de un paciente ordenadas por fecha descendente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de mediciones ordenadas de más reciente a más antigua
     */
    List<MedicionVitales> findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);

    /**
     * Obtiene la medición de signos vitales más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la medición más reciente, o vacío si no existen registros
     */
    Optional<MedicionVitales> findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);
}
