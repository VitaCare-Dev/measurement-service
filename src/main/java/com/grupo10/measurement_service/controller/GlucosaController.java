package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.dto.GlucosaRequestDto;
import com.grupo10.measurement_service.dto.GlucosaResponseDto;
import com.grupo10.measurement_service.service.GlucosaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de mediciones de glucosa.
 * Expone endpoints para registrar, consultar y eliminar registros de glucosa de pacientes.
 */
@RestController
@RequestMapping("/api/glucose")
public class GlucosaController {

    private final GlucosaService glucosaService;

    public GlucosaController(GlucosaService glucosaService) {
        this.glucosaService = glucosaService;
    }

    /**
     * Registra una nueva medición de glucosa para un paciente.
     *
     * @param request datos de la medición de glucosa a registrar
     * @return la medición registrada con estado HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<GlucosaResponseDto> registrarGlucosa(@RequestBody GlucosaRequestDto request) {
        GlucosaResponseDto response = glucosaService.registrarGlucosa(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene una medición de glucosa por su identificador.
     *
     * @param idControl identificador del control de salud asociado
     * @return la medición de glucosa encontrada con estado HTTP 200 OK
     */
    @GetMapping("/{idControl}")
    public ResponseEntity<GlucosaResponseDto> obtenerPorId(@PathVariable Long idControl) {
        return ResponseEntity.ok(glucosaService.obtenerPorId(idControl));
    }

    /**
     * Obtiene el historial completo de mediciones de glucosa de un paciente,
     * ordenado de más reciente a más antiguo.
     *
     * @param idPaciente identificador del paciente
     * @return lista de mediciones de glucosa del paciente con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<List<GlucosaResponseDto>> obtenerHistorialPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(glucosaService.obtenerHistorialPorPaciente(idPaciente));
    }

    /**
     * Obtiene la medición de glucosa más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la última medición de glucosa registrada con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}/latest")
    public ResponseEntity<GlucosaResponseDto> obtenerUltimoPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(glucosaService.obtenerUltimoPorPaciente(idPaciente));
    }

    /**
     * Elimina una medición de glucosa por su identificador.
     *
     * @param idControl identificador del control de salud a eliminar
     * @return respuesta vacía con estado HTTP 204 No Content
     */
    @DeleteMapping("/{idControl}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idControl) {
        glucosaService.eliminar(idControl);
        return ResponseEntity.noContent().build();
    }
}
