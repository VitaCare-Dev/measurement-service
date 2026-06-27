package com.grupo10.measurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo10.measurement_service.model.Lipidos;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Lipidos}.
 * Provee operaciones CRUD estándar y consultas derivadas por paciente.
 */
public interface LipidosRepository extends JpaRepository<Lipidos, Long> {

    /**
     * Obtiene todos los perfiles lipídicos de un paciente ordenados por fecha descendente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de perfiles ordenados de más reciente a más antiguo
     */
    List<Lipidos> findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);

    /**
     * Obtiene el perfil lipídico más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return el perfil más reciente, o vacío si no existen registros
     */
    Optional<Lipidos> findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);
}
