package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.dto.GlucosaRequestDto;
import com.grupo10.measurement_service.dto.GlucosaResponseDto;
import com.grupo10.measurement_service.service.GlucosaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/glucose")
public class GlucosaController {

    private final GlucosaService glucosaService;

    public GlucosaController(GlucosaService glucosaService) {
        this.glucosaService = glucosaService;
    }

    @PostMapping
    public ResponseEntity<GlucosaResponseDto> registrarGlucosa(@RequestBody GlucosaRequestDto request) {
        GlucosaResponseDto response = glucosaService.registrarGlucosa(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{idControl}")
    public ResponseEntity<GlucosaResponseDto> obtenerPorId(@PathVariable Long idControl) {
        return ResponseEntity.ok(glucosaService.obtenerPorId(idControl));
    }

    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<List<GlucosaResponseDto>> obtenerHistorialPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(glucosaService.obtenerHistorialPorPaciente(idPaciente));
    }

    @GetMapping("/patient/{idPaciente}/latest")
    public ResponseEntity<GlucosaResponseDto> obtenerUltimoPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(glucosaService.obtenerUltimoPorPaciente(idPaciente));
    }

    @DeleteMapping("/{idControl}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idControl) {
        glucosaService.eliminar(idControl);
        return ResponseEntity.noContent().build();
    }
}
