package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.GlucosaRequestDto;
import com.grupo10.measurement_service.dto.GlucosaResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Glucosa;
import com.grupo10.measurement_service.repository.GlucosaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GlucosaService {

    private final GlucosaRepository glucosaRepository;
    private final ControlSaludService controlSaludService;

    public GlucosaService(GlucosaRepository glucosaRepository, ControlSaludService controlSaludService) {
        this.glucosaRepository = glucosaRepository;
        this.controlSaludService = controlSaludService;
    }

    @Transactional
    public GlucosaResponseDto registrarGlucosa(GlucosaRequestDto request) {
        if (request.getGlucosa() <= 0) {
            throw new BusinessLogicException("El nivel de glucosa debe ser mayor que cero");
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

    public GlucosaResponseDto obtenerPorId(Long idControl) {
        Glucosa glucosa = glucosaRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición de glucosa con ID: " + idControl));
        return mapearAResponse(glucosa);
    }

    public List<GlucosaResponseDto> obtenerHistorialPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        List<Glucosa> historial = glucosaRepository
                .findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(idPaciente);
        return historial.stream().map(this::mapearAResponse).toList();
    }

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
