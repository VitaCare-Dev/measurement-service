package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.dto.MedicionVitalRequestDto;
import com.grupo10.measurement_service.dto.MedicionVitalResponseDto;
import com.grupo10.measurement_service.service.MedicionVitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vitals")
public class MedicionVitalController {

    private final MedicionVitalService medicionVitalService;

    public MedicionVitalController(MedicionVitalService medicionVitalService) {
        this.medicionVitalService = medicionVitalService;
    }

    @PostMapping
    public ResponseEntity<MedicionVitalResponseDto> registrarMedicionVital(
            @RequestBody MedicionVitalRequestDto request) {
        MedicionVitalResponseDto response = medicionVitalService.registrarMedicionVital(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{idControl}")
    public ResponseEntity<MedicionVitalResponseDto> obtenerPorId(@PathVariable Long idControl) {
        return ResponseEntity.ok(medicionVitalService.obtenerPorId(idControl));
    }

    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<List<MedicionVitalResponseDto>> obtenerHistorialPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(medicionVitalService.obtenerHistorialPorPaciente(idPaciente));
    }

    @GetMapping("/patient/{idPaciente}/latest")
    public ResponseEntity<MedicionVitalResponseDto> obtenerUltimoPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(medicionVitalService.obtenerUltimoPorPaciente(idPaciente));
    }

    @DeleteMapping("/{idControl}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idControl) {
        medicionVitalService.eliminar(idControl);
        return ResponseEntity.noContent().build();
    }
}