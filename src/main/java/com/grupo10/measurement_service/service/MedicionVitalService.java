package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.MedicionVitalRequestDto;
import com.grupo10.measurement_service.dto.MedicionVitalResponseDto;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.MedicionVitales;
import com.grupo10.measurement_service.repository.MedicionVitalesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de mediciones de signos vitales.
 * Contiene la lógica de negocio para registrar, consultar y eliminar mediciones
 * de presión arterial, temperatura y peso, vinculándolas a un control de salud padre.
 */
@Service
public class MedicionVitalService {

    private final MedicionVitalesRepository medicionVitalesRepository;
    private final ControlSaludService controlSaludService;

    public MedicionVitalService(MedicionVitalesRepository medicionVitalesRepository,
            ControlSaludService controlSaludService) {
        this.medicionVitalesRepository = medicionVitalesRepository;
        this.controlSaludService = controlSaludService;
    }

    /**
     * Registra una nueva medición de signos vitales para un paciente.
     * Crea un control de salud padre antes de persistir la medición.
     *
     * @param request datos de la medición incluyendo presión sistólica, diastólica, temperatura y peso
     * @return la medición registrada como DTO de respuesta
     * @throws BusinessLogicException si cualquier valor es nulo o menor o igual a cero
     */
    @Transactional
    public MedicionVitalResponseDto registrarMedicionVital(MedicionVitalRequestDto request) {
        if (request.getPresionSistolica() == null || request.getPresionSistolica() <= 0) {
            throw new BusinessLogicException("La presión sistólica debe ser mayor que cero");
        }
        if (request.getPresionDiastolica() == null || request.getPresionDiastolica() <= 0) {
            throw new BusinessLogicException("La presión diastólica debe ser mayor que cero");
        }
        if (request.getTemperatura() <= 0) {
            throw new BusinessLogicException("La temperatura debe ser mayor que cero");
        }
        if (request.getPeso() <= 0) {
            throw new BusinessLogicException("El peso debe ser mayor que cero");
        }

        ControlSalud controlPadre = controlSaludService.crearControl(
                request.getIdPaciente(),
                request.getNotas());

        MedicionVitales vital = new MedicionVitales();
        vital.setPresionSistolica(request.getPresionSistolica());
        vital.setPresionDiastolica(request.getPresionDiastolica());
        vital.setTemperatura(request.getTemperatura());
        vital.setPeso(request.getPeso());
        vital.setControlSalud(controlPadre);

        MedicionVitales guardado = medicionVitalesRepository.save(vital);

        return mapearAResponse(guardado);
    }

    /**
     * Obtiene una medición de signos vitales por su identificador.
     *
     * @param idControl identificador del control de salud
     * @return la medición vital como DTO de respuesta
     * @throws ResourceNotFoundException si no existe una medición con el ID indicado
     */
    public MedicionVitalResponseDto obtenerPorId(Long idControl) {
        MedicionVitales vital = medicionVitalesRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición vital con ID: " + idControl));
        return mapearAResponse(vital);
    }

    /**
     * Obtiene el historial paginado de mediciones de signos vitales de un
     * paciente, opcionalmente acotado a un rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param desde      límite inferior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param hasta      límite superior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param pageable   página, tamaño y orden solicitados
     * @return la página de mediciones que cumple los filtros; vacía si el paciente no tiene registros
     * @throws BusinessLogicException si el ID del paciente es nulo
     */
    public PageResponseDto<MedicionVitalResponseDto> obtenerHistorialPaginado(
            Long idPaciente, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        Page<MedicionVitales> pagina =
                medicionVitalesRepository.buscarHistorialPaginado(idPaciente, desde, hasta, pageable);
        List<MedicionVitalResponseDto> contenido = pagina.getContent().stream().map(this::mapearAResponse).toList();
        return new PageResponseDto<>(
                contenido, pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    /**
     * Obtiene la medición de signos vitales más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la última medición vital registrada
     * @throws BusinessLogicException    si el ID del paciente es nulo
     * @throws ResourceNotFoundException si el paciente no tiene registros de signos vitales
     */
    public MedicionVitalResponseDto obtenerUltimoPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        MedicionVitales vital = medicionVitalesRepository
                .findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontraron registros de signos vitales para el paciente con ID: " + idPaciente));
        return mapearAResponse(vital);
    }

    /**
     * Elimina una medición de signos vitales por su identificador.
     *
     * @param idControl identificador del control de salud a eliminar
     * @throws ResourceNotFoundException si no existe una medición con el ID indicado
     */
    @Transactional
    public void eliminar(Long idControl) {
        MedicionVitales vital = medicionVitalesRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición vital con ID: " + idControl));
        medicionVitalesRepository.delete(vital);
    }

    private MedicionVitalResponseDto mapearAResponse(MedicionVitales medicion) {
        MedicionVitalResponseDto response = new MedicionVitalResponseDto();

        response.setIdControl(medicion.getIdControl());
        response.setPresionSistolica(medicion.getPresionSistolica());
        response.setPresionDiastolica(medicion.getPresionDiastolica());
        response.setTemperatura(medicion.getTemperatura());
        response.setPeso(medicion.getPeso());

        response.setIdPaciente(medicion.getControlSalud().getIdPaciente());
        response.setFechaHora(medicion.getControlSalud().getFechaHora());
        response.setNotas(medicion.getControlSalud().getNotas());

        return response;
    }

}