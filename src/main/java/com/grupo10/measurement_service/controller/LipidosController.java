package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.service.LipidosService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
     * Obtiene el historial paginado de perfiles lipídicos de un paciente,
     * ordenado de más reciente a más antiguo, opcionalmente acotado a un
     * rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param desde      fecha inicial (inclusive) del rango a consultar, en formato {@code yyyy-MM-dd}
     * @param hasta      fecha final (inclusive) del rango a consultar, en formato {@code yyyy-MM-dd}
     * @param pageable   número de página y tamaño solicitados (parámetros {@code page}/{@code size})
     * @return la página de perfiles lipídicos del paciente con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<PageResponseDto<LipidosResponseDto>> obtenerHistorialPorPaciente(
            @PathVariable Long idPaciente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @PageableDefault(size = 10, sort = "controlSalud.fechaHora", direction = Sort.Direction.DESC)
            Pageable pageable) {
        LocalDateTime desdeInicioDia = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaFinDia = hasta != null ? hasta.atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(
                lipidosService.obtenerHistorialPaginado(idPaciente, desdeInicioDia, hastaFinDia, pageable));
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