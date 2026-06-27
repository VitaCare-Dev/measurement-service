package com.grupo10.measurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo10.measurement_service.model.ControlSalud;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link ControlSalud}.
 * Provee operaciones CRUD estándar y consultas por paciente.
 */
public interface ControlSaludRepository extends JpaRepository<ControlSalud, Long> {

    /**
     * Obtiene todos los controles de salud de un paciente ordenados por fecha descendente.
     *
     * @param idPaciente identificador del paciente
     * @return lista de controles ordenados de más reciente a más antiguo
     */
    List<ControlSalud> findByIdPacienteOrderByFechaHoraDesc(Long idPaciente);
}
