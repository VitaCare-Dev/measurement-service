package com.grupo10.measurement_service.controller;

import com.grupo10.measurement_service.dto.MedicionVitalRequestDto;
import com.grupo10.measurement_service.dto.MedicionVitalResponseDto;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.service.MedicionVitalService;
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
 * Controlador REST para la gestión de mediciones de signos vitales.
 * Expone endpoints para registrar, consultar y eliminar registros de presión arterial,
 * temperatura y peso de pacientes.
 */
@RestController
@RequestMapping("/api/vitals")
public class MedicionVitalController {

    private final MedicionVitalService medicionVitalService;

    public MedicionVitalController(MedicionVitalService medicionVitalService) {
        this.medicionVitalService = medicionVitalService;
    }

    /**
     * Registra una nueva medición de signos vitales para un paciente.
     *
     * @param request datos de la medición vital a registrar
     * @return la medición registrada con estado HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<MedicionVitalResponseDto> registrarMedicionVital(
            @RequestBody MedicionVitalRequestDto request) {
        MedicionVitalResponseDto response = medicionVitalService.registrarMedicionVital(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene una medición de signos vitales por su identificador.
     *
     * @param idControl identificador del control de salud asociado
     * @return la medición vital encontrada con estado HTTP 200 OK
     */
    @GetMapping("/{idControl}")
    public ResponseEntity<MedicionVitalResponseDto> obtenerPorId(@PathVariable Long idControl) {
        return ResponseEntity.ok(medicionVitalService.obtenerPorId(idControl));
    }

    /**
     * Obtiene el historial paginado de signos vitales de un paciente,
     * ordenado de más reciente a más antiguo, opcionalmente acotado a un
     * rango de fechas.
     *
     * @param idPaciente identificador del paciente
     * @param desde      fecha inicial (inclusive) del rango a consultar, en formato {@code yyyy-MM-dd}
     * @param hasta      fecha final (inclusive) del rango a consultar, en formato {@code yyyy-MM-dd}
     * @param pageable   número de página y tamaño solicitados (parámetros {@code page}/{@code size})
     * @return la página de mediciones vitales del paciente con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}")
    public ResponseEntity<PageResponseDto<MedicionVitalResponseDto>> obtenerHistorialPorPaciente(
            @PathVariable Long idPaciente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @PageableDefault(size = 10, sort = "controlSalud.fechaHora", direction = Sort.Direction.DESC)
            Pageable pageable) {
        LocalDateTime desdeInicioDia = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaFinDia = hasta != null ? hasta.atTime(LocalTime.MAX) : null;
        return ResponseEntity.ok(
                medicionVitalService.obtenerHistorialPaginado(idPaciente, desdeInicioDia, hastaFinDia, pageable));
    }

    /**
     * Obtiene la medición de signos vitales más reciente de un paciente.
     *
     * @param idPaciente identificador del paciente
     * @return la última medición vital registrada con estado HTTP 200 OK
     */
    @GetMapping("/patient/{idPaciente}/latest")
    public ResponseEntity<MedicionVitalResponseDto> obtenerUltimoPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(medicionVitalService.obtenerUltimoPorPaciente(idPaciente));
    }

    /**
     * Elimina una medición de signos vitales por su identificador.
     *
     * @param idControl identificador del control de salud a eliminar
     * @return respuesta vacía con estado HTTP 204 No Content
     */
    @DeleteMapping("/{idControl}")
    public ResponseEntity<Void> eliminar(@PathVariable Long idControl) {
        medicionVitalService.eliminar(idControl);
        return ResponseEntity.noContent().build();
    }
}