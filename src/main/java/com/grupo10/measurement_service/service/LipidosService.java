package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Lipidos;
import com.grupo10.measurement_service.repository.LipidosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LipidosService {

    private final LipidosRepository lipidosRepository;
    private final ControlSaludService controlSaludService;

    public LipidosService(LipidosRepository lipidosRepository, ControlSaludService controlSaludService) {
        this.lipidosRepository = lipidosRepository;
        this.controlSaludService = controlSaludService;
    }

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

    public LipidosResponseDto obtenerPorId(Long idControl) {
        Lipidos lipidos = lipidosRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición de lípidos con ID: " + idControl));
        return mapearAResponse(lipidos);
    }

    public List<LipidosResponseDto> obtenerHistorialPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        List<Lipidos> historial = lipidosRepository
                .findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(idPaciente);
        if (historial.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron registros de lípidos para el paciente con ID: " + idPaciente);
        }
        return historial.stream().map(this::mapearAResponse).toList();
    }

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

    @Transactional
    public void eliminar(Long idControl) {
        if (!lipidosRepository.existsById(idControl)) {
            throw new ResourceNotFoundException(
                    "No se encontró medición de lípidos con ID: " + idControl);
        }
        lipidosRepository.deleteById(idControl);
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