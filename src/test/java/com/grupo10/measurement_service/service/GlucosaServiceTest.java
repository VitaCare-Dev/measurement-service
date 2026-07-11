package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.GlucosaRequestDto;
import com.grupo10.measurement_service.dto.GlucosaResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Glucosa;
import com.grupo10.measurement_service.model.PeriodoGlucosa;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.repository.GlucosaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlucosaServiceTest {

    @Mock
    private GlucosaRepository glucosaRepository;

    @Mock
    private ControlSaludService controlSaludService;

    @InjectMocks
    private GlucosaService glucosaService;

    private ControlSalud crearControl() {
        ControlSalud control = new ControlSalud();
        control.setIdControl(1L);
        control.setIdPaciente(1L);
        control.setFechaHora(LocalDateTime.now());
        control.setNotas("nota");
        return control;
    }

    private Glucosa crearGlucosa(ControlSalud control) {
        Glucosa glucosa = new Glucosa();
        glucosa.setIdControl(1L);
        glucosa.setNivelGlucosa(95);
        glucosa.setPeriodo(PeriodoGlucosa.AYUNAS);
        glucosa.setControlSalud(control);
        return glucosa;
    }

    @Test
    void registrarGlucosa_GlucosaMenorOIgualCero_LanzaBusinessLogicException() {
        GlucosaRequestDto request = new GlucosaRequestDto();
        request.setGlucosa(0);
        assertThrows(BusinessLogicException.class, () -> glucosaService.registrarGlucosa(request));
    }

    @Test
    void registrarGlucosa_GlucosaSobreElMaximoPlausible_LanzaBusinessLogicException() {
        GlucosaRequestDto request = new GlucosaRequestDto();
        request.setGlucosa(99999);
        request.setPeriodo(PeriodoGlucosa.AYUNAS);
        assertThrows(BusinessLogicException.class, () -> glucosaService.registrarGlucosa(request));
    }

    @Test
    void registrarGlucosa_PeriodoNulo_LanzaBusinessLogicException() {
        GlucosaRequestDto request = new GlucosaRequestDto();
        request.setGlucosa(95);
        request.setPeriodo(null);
        assertThrows(BusinessLogicException.class, () -> glucosaService.registrarGlucosa(request));
    }

    @Test
    void registrarGlucosa_DatosValidos_RetornaResponse() {
        GlucosaRequestDto request = new GlucosaRequestDto();
        request.setIdPaciente(1L);
        request.setGlucosa(95);
        request.setPeriodo(PeriodoGlucosa.AYUNAS);
        request.setNotas("nota");

        ControlSalud control = crearControl();
        Glucosa glucosa = crearGlucosa(control);

        when(controlSaludService.crearControl(1L, "nota")).thenReturn(control);
        when(glucosaRepository.save(any(Glucosa.class))).thenReturn(glucosa);

        GlucosaResponseDto response = glucosaService.registrarGlucosa(request);

        assertNotNull(response);
        assertEquals(95, response.getGlucosa());
        assertEquals(PeriodoGlucosa.AYUNAS, response.getPeriodo());
        assertEquals(1L, response.getIdPaciente());
        verify(glucosaRepository).save(any(Glucosa.class));
    }

    @Test
    void obtenerPorId_Encontrado_RetornaResponse() {
        ControlSalud control = crearControl();
        Glucosa glucosa = crearGlucosa(control);
        when(glucosaRepository.findById(1L)).thenReturn(Optional.of(glucosa));

        GlucosaResponseDto response = glucosaService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getIdControl());
    }

    @Test
    void obtenerPorId_NoEncontrado_LanzaResourceNotFoundException() {
        when(glucosaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> glucosaService.obtenerPorId(99L));
    }

    @Test
    void obtenerHistorialPaginado_IdNulo_LanzaBusinessLogicException() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(BusinessLogicException.class,
                () -> glucosaService.obtenerHistorialPaginado(null, null, null, pageable));
    }

    @Test
    void obtenerHistorialPaginado_SinRegistros_RetornaPaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        when(glucosaRepository.buscarHistorialPaginado(1L, null, null, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        PageResponseDto<GlucosaResponseDto> resultado =
                glucosaService.obtenerHistorialPaginado(1L, null, null, pageable);

        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());
        assertEquals(0, resultado.getTotalPages());
    }

    @Test
    void obtenerHistorialPaginado_ConRegistros_RetornaPaginaConContenido() {
        ControlSalud control = crearControl();
        Glucosa glucosa = crearGlucosa(control);
        Pageable pageable = PageRequest.of(0, 10);
        when(glucosaRepository.buscarHistorialPaginado(1L, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(glucosa), pageable, 1));

        PageResponseDto<GlucosaResponseDto> resultado =
                glucosaService.obtenerHistorialPaginado(1L, null, null, pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals(95, resultado.getContent().get(0).getGlucosa());
        assertEquals(1, resultado.getTotalElements());
        assertEquals(0, resultado.getPage());
        assertEquals(10, resultado.getSize());
    }

    @Test
    void obtenerHistorialPaginado_ConRangoDeFechas_PropagaLosLimites() {
        LocalDateTime desde = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2026, 1, 31, 23, 59);
        Pageable pageable = PageRequest.of(0, 10);
        when(glucosaRepository.buscarHistorialPaginado(1L, desde, hasta, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        glucosaService.obtenerHistorialPaginado(1L, desde, hasta, pageable);

        verify(glucosaRepository).buscarHistorialPaginado(1L, desde, hasta, pageable);
    }

    @Test
    void obtenerUltimoPorPaciente_IdNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> glucosaService.obtenerUltimoPorPaciente(null));
    }

    @Test
    void obtenerUltimoPorPaciente_Encontrado_RetornaResponse() {
        ControlSalud control = crearControl();
        Glucosa glucosa = crearGlucosa(control);
        when(glucosaRepository.findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(Optional.of(glucosa));

        GlucosaResponseDto response = glucosaService.obtenerUltimoPorPaciente(1L);

        assertNotNull(response);
        assertEquals(95, response.getGlucosa());
    }

    @Test
    void obtenerUltimoPorPaciente_NoEncontrado_LanzaResourceNotFoundException() {
        when(glucosaRepository.findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(99L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> glucosaService.obtenerUltimoPorPaciente(99L));
    }

    @Test
    void eliminar_Encontrado_EliminaCorrectamente() {
        ControlSalud control = crearControl();
        Glucosa glucosa = crearGlucosa(control);
        when(glucosaRepository.findById(1L)).thenReturn(Optional.of(glucosa));

        glucosaService.eliminar(1L);

        verify(glucosaRepository).delete(glucosa);
    }

    @Test
    void eliminar_NoEncontrado_LanzaResourceNotFoundException() {
        when(glucosaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> glucosaService.eliminar(99L));
    }
}
