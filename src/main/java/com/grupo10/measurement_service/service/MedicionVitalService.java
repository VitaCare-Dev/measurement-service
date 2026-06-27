package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.MedicionVitalRequestDto;
import com.grupo10.measurement_service.dto.MedicionVitalResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.MedicionVitales;
import com.grupo10.measurement_service.repository.MedicionVitalesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicionVitalService {

    private final MedicionVitalesRepository medicionVitalesRepository;
    private final ControlSaludService controlSaludService;

    public MedicionVitalService(MedicionVitalesRepository medicionVitalesRepository,
            ControlSaludService controlSaludService) {
        this.medicionVitalesRepository = medicionVitalesRepository;
        this.controlSaludService = controlSaludService;
    }

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

    public MedicionVitalResponseDto obtenerPorId(Long idControl) {
        MedicionVitales vital = medicionVitalesRepository.findById(idControl)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró medición vital con ID: " + idControl));
        return mapearAResponse(vital);
    }

    public List<MedicionVitalResponseDto> obtenerHistorialPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }
        List<MedicionVitales> historial = medicionVitalesRepository
                .findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(idPaciente);
        return historial.stream().map(this::mapearAResponse).toList();
    }

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