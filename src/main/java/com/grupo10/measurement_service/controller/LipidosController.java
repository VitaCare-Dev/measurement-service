package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.service.LipidosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lipids")
public class LipidosController {

    private final LipidosService lipidosService;

    public LipidosController(LipidosService lipidosService) {
        this.lipidosService = lipidosService;
    }

    @PostMapping
    public ResponseEntity<LipidosResponseDto> registrarLipidos(@RequestBody LipidosRequestDto request) {
        LipidosResponseDto response = lipidosService.registrarLipidos(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{idControl}")
    public ResponseEntity<LipidosResponseDto> obtenerPorId(@PathVariable Long idControl) {
        return ResponseEntity.ok(lipidosService.obtenerPorId(idControl));
    }

    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<List<LipidosResponseDto>> obtenerHistorialPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(lipidosService.obtenerHistorialPorPaciente(idPaciente));
    }

    @GetMapping("/patient/{idPaciente}/latest")
    public ResponseEntity<LipidosResponseDto> obtenerUltimoPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(lipidosService.obtenerUltimoPorPaciente(idPaciente));
    }

    @DeleteMapping("/{idControl}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idControl) {
        lipidosService.eliminar(idControl);
        return ResponseEntity.noContent().build();
    }
}