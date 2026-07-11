package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.GlucosaRequestDto;
import com.grupo10.measurement_service.dto.GlucosaResponseDto;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Glucosa;
import com.grupo10.measurement_service.repository.GlucosaRepository;
import com.grupo10.measurement_service.utils.RangosMedicionValidos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para la gestión de mediciones de glucosa.
 * Contiene la lógica de negocio para registrar, consultar y eliminar
 * mediciones de glucosa, vinculándolas a un control de salud padre.
 */
@Service
public class GlucosaService {

    private final GlucosaRepository glucosaRepository;
    private final ControlSaludService controlSaludService;

    public GlucosaService(GlucosaRepository glucosaRepository, ControlSaludService controlSaludService) {
        this.glucosaRepository = glucosaRepository;
        this.controlSaludService = controlSaludService;
    }

    /**
     * Registra una nueva medición de glucosa para un paciente.
     * Crea un control de salud padre antes de persistir la medición.
     *
     * @param request datos de la medición incluyendo nivel de glucosa y periodo
     * @return la medición registrada como DTO de respuesta
     * @throws BusinessLogicException si el nivel de glucosa es menor o igual a cero,
     *                                o si el periodo de medición es nulo
     */
    @Transactional
    public GlucosaResponseDto registrarGlucosa(GlucosaRequestDto request) {
        if (request.getGlucosa() < RangosMedicionValidos.GLUCOSA_MIN
                || request.getGlucosa() > RangosMedicionValidos.GLUCOSA_MAX) {
            throw new BusinessLogicException("El nivel de glucosa debe estar entre "
                    + RangosMedicionValidos.GLUCOSA_MIN + " y " + RangosMedicionValidos.GLUCOSA_MAX + " mg/dL");
        }
        if (request.getPeriodo() == null) {
            throw new BusinessLogicException("El periodo de medición es obligatorio. Valores válidos: AYUNAS, POSTPRANDIAL, NOCTURNA, ALEATORIO");
        }

        ControlSalud controlPadre = controlSaludService.crearControl(
                request.getIdPaciente(),
                request.getNotas());

        Glucosa glucosa = new Glucosa();
        glucosa.setNivelGlucosa(request.getGlucosa());
        glucosa.setPeriodo(request.getPeriodo());
        glucosa.setControlSalud(controlPadre);

        Glucosa glucosaGuardada = glucosaRepository.save(glucosa);

        return mapearAResponse(glucosaGuardada);
    }

    /**
     * Obtiene una medición de glucosa por su identificador.
     *
     * @param idControl identificador del control de salud
     * @return la medición de glucosa como DTO de respuesta
     * @throws ResourceNotFoundException si no existe una medición con el ID indicado
     */
    public GlucosaResponseDto obtenerPorId(Long idControl) {
        Glucosa glucosa = glucosaRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición de glucosa con ID: " + idControl));
        return mapearAResponse(glucosa);
    }

    /**
     * Obtiene el historial paginado de mediciones de glucosa de un paciente,
     * opcionalmente acotado a un rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param desde      límite inferior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param hasta      límite superior (inclusive) del rango de fechas, o {@code null} para no acotar
     * @param pageable   página, tamaño y orden solicitados
     * @return la página de mediciones que cumple los filtros; vacía si el paciente no tiene registros
     * @throws BusinessLogicException si el ID del paciente es nulo
     */
    public PageResponseDto<GlucosaResponseDto> obtenerHistorialPaginado(
            Long idPaciente, LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        Page<Glucosa> pagina = glucosaRepository.buscarHistorialPaginado(idPaciente, desde, hasta, pageable);
        List<GlucosaResponseDto> contenido = pagina.getContent().stream().map(this::mapearAResponse).toList();
        return new PageResponseDto<>(
                contenido, pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
    }

    /**
     * Obtiene la medición de glucosa más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la última medición de glucosa registrada
     * @throws BusinessLogicException    si el ID del paciente es nulo
     * @throws ResourceNotFoundException si el paciente no tiene registros de glucosa
     */
    public GlucosaResponseDto obtenerUltimoPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        Glucosa glucosa = glucosaRepository
                .findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontraron registros de glucosa para el paciente con ID: " + idPaciente));
        return mapearAResponse(glucosa);
    }

    /**
     * Elimina una medición de glucosa por su identificador.
     *
     * @param idControl identificador del control de salud a eliminar
     * @throws ResourceNotFoundException si no existe una medición con el ID indicado
     */
    @Transactional
    public void eliminar(Long idControl) {
        Glucosa glucosa = glucosaRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición de glucosa con ID: " + idControl));
        glucosaRepository.delete(glucosa);
    }

    private GlucosaResponseDto mapearAResponse(Glucosa medicion) {
        GlucosaResponseDto response = new GlucosaResponseDto();

        response.setIdControl(medicion.getIdControl());
        response.setGlucosa(medicion.getNivelGlucosa());
        response.setPeriodo(medicion.getPeriodo());

        response.setIdPaciente(medicion.getControlSalud().getIdPaciente());
        response.setFechaHora(medicion.getControlSalud().getFechaHora());
        response.setNotas(medicion.getControlSalud().getNotas());

        return response;
    }

}
