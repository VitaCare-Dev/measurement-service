package com.grupo10.measurement_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.repository.ControlSaludRepository;

@Service
public class ControlSaludService {

    private final ControlSaludRepository controlSaludRepository;

    public ControlSaludService(ControlSaludRepository controlSaludRepository) {
        this.controlSaludRepository = controlSaludRepository;
    }

    public ControlSalud crearControl(Long idPaciente, String notas) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }

        ControlSalud controlSalud = new ControlSalud();
        controlSalud.setIdPaciente(idPaciente);
        controlSalud.setFechaHora(LocalDateTime.now());
        controlSalud.setNotas(notas);

        return controlSaludRepository.save(controlSalud);
    }

    public List<ControlSalud> obtenerHistorialPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }

        List<ControlSalud> historial = controlSaludRepository.findByIdPacienteOrderByFechaHoraDesc(idPaciente);

        if (historial.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró historial para el paciente con ID: " + idPaciente);
        }

        return historial;
    }

}
