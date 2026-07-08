package com.grupo10.measurement_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.grupo10.measurement_service.model.MedicionVitales;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link MedicionVitales}.
 * Provee operaciones CRUD estándar y consultas derivadas por paciente.
 */
public interface MedicionVitalesRepository extends JpaRepository<MedicionVitales, Long> {

    /**
     * Busca el historial paginado de mediciones de signos vitales de un
     * paciente, opcionalmente acotado a un rango de fechas. El orden se
     * resuelve según el {@link Pageable} recibido (por defecto, fecha
     * descendente).
     *
     * @param idPaciente identificador del paciente
     * @param desde      límite inferior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param hasta      límite superior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param pageable   página, tamaño y orden solicitados
     * @return la página de mediciones que cumple los filtros
     */
    @Query("SELECT m FROM MedicionVitales m WHERE m.controlSalud.idPaciente = :idPaciente "
            + "AND (:desde IS NULL OR m.controlSalud.fechaHora >= :desde) "
            + "AND (:hasta IS NULL OR m.controlSalud.fechaHora <= :hasta)")
    Page<MedicionVitales> buscarHistorialPaginado(
            @Param("idPaciente") Long idPaciente,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);

    /**
     * Obtiene la medición de signos vitales más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la medición más reciente, o vacío si no existen registros
     */
    Optional<MedicionVitales> findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(Long idPaciente);
}
