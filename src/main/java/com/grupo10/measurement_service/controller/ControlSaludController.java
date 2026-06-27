package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.service.ControlSaludService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la consulta del historial de controles de salud.
 * Permite obtener todos los registros de salud asociados a un paciente,
 * independientemente del tipo de medición.
 */
@RestController
@RequestMapping("/api/controls")
public class ControlSaludController {

    private final ControlSaludService controlSaludService;

    public ControlSaludController(ControlSaludService controlSaludService) {
        this.controlSaludService = controlSaludService;
    }

    /**
     * Obtiene el historial completo de controles de salud de un paciente,
     * ordenado de más reciente a más antiguo.
     *
     * @param idPaciente identificador del paciente
     * @return lista de controles de salud del paciente con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<List<ControlSalud>> obtenerHistorialPaciente(@PathVariable Long idPaciente) {
        List<ControlSalud> historial = controlSaludService.obtenerHistorialPorPaciente(idPaciente);
        return new ResponseEntity<>(historial, HttpStatus.OK);

    }
}