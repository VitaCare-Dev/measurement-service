package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.service.LipidosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de mediciones de lípidos.
 * Expone endpoints para registrar, consultar y eliminar perfiles lipídicos de pacientes.
 */
@RestController
@RequestMapping("/api/lipids")
public class LipidosController {

    private final LipidosService lipidosService;

    public LipidosController(LipidosService lipidosService) {
        this.lipidosService = lipidosService;
    }

    /**
     * Registra un nuevo perfil lipídico para un paciente.
     *
     * @param request datos del perfil lipídico a registrar
     * @return el perfil lipídico registrado con estado HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<LipidosResponseDto> registrarLipidos(@RequestBody LipidosRequestDto request) {
        LipidosResponseDto response = lipidosService.registrarLipidos(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene un registro de lípidos por su identificador.
     *
     * @param idControl identificador del control de salud asociado
     * @return el registro de lípidos encontrado con estado HTTP 200 OK
     */
    @GetMapping("/{idControl}")
    public ResponseEntity<LipidosResponseDto> obtenerPorId(@PathVariable Long idControl) {
        return ResponseEntity.ok(lipidosService.obtenerPorId(idControl));
    }

    /**
     * Obtiene el historial completo de perfiles lipídicos de un paciente,
     * ordenado de más reciente a más antiguo.
     *
     * @param idPaciente identificador del paciente
     * @return lista de perfiles lipídicos del paciente con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<List<LipidosResponseDto>> obtenerHistorialPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(lipidosService.obtenerHistorialPorPaciente(idPaciente));
    }

    /**
     * Obtiene el perfil lipídico más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return el último perfil lipídico registrado con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}/latest")
    public ResponseEntity<LipidosResponseDto> obtenerUltimoPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(lipidosService.obtenerUltimoPorPaciente(idPaciente));
    }

    /**
     * Elimina un registro de lípidos por su identificador.
     *
     * @param idControl identificador del control de salud a eliminar
     * @return respuesta vacía con estado HTTP 204 No Content
     */
    @DeleteMapping("/{idControl}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idControl) {
        lipidosService.eliminar(idControl);
        return ResponseEntity.noContent().build();
    }
}