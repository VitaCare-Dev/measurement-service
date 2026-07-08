package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Lipidos;
import com.grupo10.measurement_service.repository.LipidosRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de mediciones de lípidos.
 * Contiene la lógica de negocio para registrar, consultar y eliminar
 * perfiles lipídicos, vinculándolos a un control de salud padre.
 */
@Service
public class LipidosService {

    private final LipidosRepository lipidosRepository;
    private final ControlSaludService controlSaludService;

    public LipidosService(LipidosRepository lipidosRepository, ControlSaludService controlSaludService) {
        this.lipidosRepository = lipidosRepository;
        this.controlSaludService = controlSaludService;
    }

    /**
     * Registra un nuevo perfil lipídico para un paciente.
     * Crea un control de salud padre antes de persistir la medición.
     *
     * @param request datos del perfil lipídico incluyendo colesterol total, LDL, HDL y triglicéridos
     * @return el perfil lipídico registrado como DTO de respuesta
     * @throws BusinessLogicException si cualquier valor del perfil es menor o igual a cero
     */
    @Transactional
    public LipidosResponseDto registrarLipidos(LipidosRequestDto request) {
        if (request.getColesterolTotal() <= 0) {
            throw new BusinessLogicException("El colesterol total debe ser mayor que cero");
        }
        if (request.getColesterolLDL() <= 0) {
            throw new BusinessLogicException("El colesterol LDL debe ser mayor que cero");
        }
        if (request.getColesterolHDL() <= 0) {
            throw new BusinessLogicException("El colesterol HDL debe ser mayor que cero");
        }
        if (request.getTrigliceridos() <= 0) {
            throw new BusinessLogicException("Los triglicéridos deben ser mayores que cero");
        }

        ControlSalud controlPadre = controlSaludService.crearControl(
                request.getIdPaciente(),
                request.getNotas());

        Lipidos lipidos = new Lipidos();
        lipidos.setColesterolTotal(request.getColesterolTotal());
        lipidos.setColesterolLDL(request.getColesterolLDL());
        lipidos.setColesterolHDL(request.getColesterolHDL());
        lipidos.setTrigliceridos(request.getTrigliceridos());
        lipidos.setControlSalud(controlPadre);

        Lipidos guardado = lipidosRepository.save(lipidos);

        return mapearAResponse(guardado);
    }

    /**
     * Obtiene un registro de lípidos por su identificador.
     *
     * @param idControl identificador del control de salud
     * @return el perfil lipídico como DTO de respuesta
     * @throws ResourceNotFoundException si no existe un registro con el ID indicado
     */
    public LipidosResponseDto obtenerPorId(Long idControl) {
        Lipidos lipidos = lipidosRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición de lípidos con ID: " + idControl));
        return mapearAResponse(lipidos);
    }

    /**
     * Obtiene el historial paginado de perfiles lipídicos de un paciente,
     * opcionalmente acotado a un rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param desde      límite inferior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param hasta      límite superior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param pageable   página, tamaño y orden solicitados
     * @return la página de perfiles que cumple los filtros; vacía si el paciente no tiene registros
     * @throws BusinessLogicException si el ID del paciente es nulo
     */
    public PageResponseDto<LipidosResponseDto> obtenerHistorialPaginado(
            Long idPaciente, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        Page<Lipidos> pagina = lipidosRepository.buscarHistorialPaginado(idPaciente, desde, hasta, pageable);
        List<LipidosResponseDto> contenido = pagina.getContent().stream().map(this::mapearAResponse).toList();
        return new PageResponseDto<>(
                contenido, pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    /**
     * Obtiene el perfil lipídico más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return el último perfil lipídico registrado
     * @throws BusinessLogicException    si el ID del paciente es nulo
     * @throws ResourceNotFoundException si el paciente no tiene registros de lípidos
     */
    public LipidosResponseDto obtenerUltimoPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        Lipidos lipidos = lipidosRepository
                .findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontraron registros de lípidos para el paciente con ID: " + idPaciente));
        return mapearAResponse(lipidos);
    }

    /**
     * Elimina un registro de lípidos por su identificador.
     *
     * @param idControl identificador del control de salud a eliminar
     * @throws ResourceNotFoundException si no existe un registro con el ID indicado
     */
    @Transactional
    public void eliminar(Long idControl) {
        Lipidos lipidos = lipidosRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición de lípidos con ID: " + idControl));
        lipidosRepository.delete(lipidos);
    }

    private LipidosResponseDto mapearAResponse(Lipidos medicion) {
        LipidosResponseDto response = new LipidosResponseDto();

        response.setIdControl(medicion.getIdControl());
        response.setColesterolTotal(medicion.getColesterolTotal());
        response.setColesterolLDL(medicion.getColesterolLDL());
        response.setColesterolHDL(medicion.getColesterolHDL());
        response.setTrigliceridos(medicion.getTrigliceridos());
        response.setIdPaciente(medicion.getControlSalud().getIdPaciente());
        response.setFechaHora(medicion.getControlSalud().getFechaHora());
        response.setNotas(medicion.getControlSalud().getNotas());

        return response;
    }

}