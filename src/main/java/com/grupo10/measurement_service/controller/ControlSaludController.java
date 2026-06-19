package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.service.ControlSaludService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/controls") 
public class ControlSaludController {

    private final ControlSaludService controlSaludService;

    public ControlSaludController(ControlSaludService controlSaludService) {
        this.controlSaludService = controlSaludService;
    }

    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<List<ControlSalud>> obtenerHistorialPaciente(@PathVariable Long idPaciente) {
        List<ControlSalud> historial = controlSaludService.obtenerHistorialPorPaciente(idPaciente);
        return new ResponseEntity<>(historial, HttpStatus.OK);
        
    }
}