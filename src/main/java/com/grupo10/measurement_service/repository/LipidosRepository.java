package com.grupo10.measurement_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.grupo10.measurement_service.model.Lipidos;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Lipidos}.
 * Provee operaciones CRUD estándar y consultas derivadas por paciente.
 */
public interface LipidosRepository extends JpaRepository<Lipidos, Long> {

    /**
     * Busca el historial paginado de perfiles lipídicos de un paciente,
     * opcionalmente acotado a un rango de fechas. El orden se resuelve según
     * el {@link Pageable} recibido (por defecto, fecha descendente).
     *
     * @param idPaciente identificador del paciente
     * @param desde      límite inferior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param hasta      límite superior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param pageable   página, tamaño y orden solicitados
     * @return la página de perfiles que cumple los filtros
     */
    @Query("SELECT l FROM Lipidos l WHERE l.controlSalud.idPaciente = :idPaciente "
            + "AND (:desde IS NULL OR l.controlSalud.fechaHora >= :desde) "
            + "AND (:hasta IS NULL OR l.controlSalud.fechaHora <= :hasta)")
    Page<Lipidos> buscarHistorialPaginado(
            @Param("idPaciente") Long idPaciente,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);

    /**
     * Obtiene el perfil lipídico más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return el perfil más reciente, o vacío si no existen registros
     */
    Optional<Lipidos> findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);
}
