package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Lipidos;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.repository.LipidosRepository;
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
class LipidosServiceTest {

    @Mock
    private LipidosRepository lipidosRepository;

    @Mock
    private ControlSaludService controlSaludService;

    @InjectMocks
    private LipidosService lipidosService;

    private ControlSalud crearControl() {
        ControlSalud control = new ControlSalud();
        control.setIdControl(1L);
        control.setIdPaciente(1L);
        control.setFechaHora(LocalDateTime.now());
        control.setNotas("nota");
        return control;
    }

    private Lipidos crearLipidos(ControlSalud control) {
        Lipidos lipidos = new Lipidos();
        lipidos.setIdControl(1L);
        lipidos.setColesterolTotal(180);
        lipidos.setColesterolLDL(110);
        lipidos.setColesterolHDL(55);
        lipidos.setTrigliceridos(130);
        lipidos.setControlSalud(control);
        return lipidos;
    }

    private LipidosRequestDto crearRequest() {
        LipidosRequestDto request = new LipidosRequestDto();
        request.setIdPaciente(1L);
        request.setNotas("nota");
        request.setColesterolTotal(180);
        request.setColesterolLDL(110);
        request.setColesterolHDL(55);
        request.setTrigliceridos(130);
        return request;
    }

    @Test
    void registrarLipidos_ColesterolTotalMenorOIgualCero_LanzaBusinessLogicException() {
        LipidosRequestDto request = crearRequest();
        request.setColesterolTotal(0);
        assertThrows(BusinessLogicException.class, () -> lipidosService.registrarLipidos(request));
    }

    @Test
    void registrarLipidos_ColesterolLDLMenorOIgualCero_LanzaBusinessLogicException() {
        LipidosRequestDto request = crearRequest();
        request.setColesterolLDL(0);
        assertThrows(BusinessLogicException.class, () -> lipidosService.registrarLipidos(request));
    }

    @Test
    void registrarLipidos_ColesterolHDLMenorOIgualCero_LanzaBusinessLogicException() {
        LipidosRequestDto request = crearRequest();
        request.setColesterolHDL(0);
        assertThrows(BusinessLogicException.class, () -> lipidosService.registrarLipidos(request));
    }

    @Test
    void registrarLipidos_TrigliceriodosMenorOIgualCero_LanzaBusinessLogicException() {
        LipidosRequestDto request = crearRequest();
        request.setTrigliceridos(0);
        assertThrows(BusinessLogicException.class, () -> lipidosService.registrarLipidos(request));
    }

    @Test
    void registrarLipidos_ColesterolTotalSobreElMaximoPlausible_LanzaBusinessLogicException() {
        LipidosRequestDto request = crearRequest();
        request.setColesterolTotal(99999);
        assertThrows(BusinessLogicException.class, () -> lipidosService.registrarLipidos(request));
    }

    @Test
    void registrarLipidos_DatosValidos_RetornaResponse() {
        LipidosRequestDto request = crearRequest();
        ControlSalud control = crearControl();
        Lipidos lipidos = crearLipidos(control);

        when(controlSaludService.crearControl(1L, "nota")).thenReturn(control);
        when(lipidosRepository.save(any(Lipidos.class))).thenReturn(lipidos);

        LipidosResponseDto response = lipidosService.registrarLipidos(request);

        assertNotNull(response);
        assertEquals(180, response.getColesterolTotal());
        assertEquals(1L, response.getIdPaciente());
        verify(lipidosRepository).save(any(Lipidos.class));
    }

    @Test
    void obtenerPorId_Encontrado_RetornaResponse() {
        ControlSalud control = crearControl();
        Lipidos lipidos = crearLipidos(control);
        when(lipidosRepository.findById(1L)).thenReturn(Optional.of(lipidos));

        LipidosResponseDto response = lipidosService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getIdControl());
    }

    @Test
    void obtenerPorId_NoEncontrado_LanzaResourceNotFoundException() {
        when(lipidosRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lipidosService.obtenerPorId(99L));
    }

    @Test
    void obtenerHistorialPaginado_IdNulo_LanzaBusinessLogicException() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(BusinessLogicException.class,
                () -> lipidosService.obtenerHistorialPaginado(null, null, null, pageable));
    }

    @Test
    void obtenerHistorialPaginado_SinRegistros_RetornaPaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        when(lipidosRepository.buscarHistorialPaginado(1L, null, null, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        PageResponseDto<LipidosResponseDto> resultado =
                lipidosService.obtenerHistorialPaginado(1L, null, null, pageable);

        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());
    }

    @Test
    void obtenerHistorialPaginado_ConRegistros_RetornaPaginaConContenido() {
        ControlSalud control = crearControl();
        Lipidos lipidos = crearLipidos(control);
        Pageable pageable = PageRequest.of(0, 10);
        when(lipidosRepository.buscarHistorialPaginado(1L, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(lipidos), pageable, 1));

        PageResponseDto<LipidosResponseDto> resultado =
                lipidosService.obtenerHistorialPaginado(1L, null, null, pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals(180, resultado.getContent().get(0).getColesterolTotal());
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void obtenerHistorialPaginado_ConRangoDeFechas_PropagaLosLimites() {
        LocalDateTime desde = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2026, 1, 31, 23, 59);
        Pageable pageable = PageRequest.of(0, 10);
        when(lipidosRepository.buscarHistorialPaginado(1L, desde, hasta, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        lipidosService.obtenerHistorialPaginado(1L, desde, hasta, pageable);

        verify(lipidosRepository).buscarHistorialPaginado(1L, desde, hasta, pageable);
    }

    @Test
    void obtenerUltimoPorPaciente_IdNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> lipidosService.obtenerUltimoPorPaciente(null));
    }

    @Test
    void obtenerUltimoPorPaciente_Encontrado_RetornaResponse() {
        ControlSalud control = crearControl();
        Lipidos lipidos = crearLipidos(control);
        when(lipidosRepository.findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(Optional.of(lipidos));

        LipidosResponseDto response = lipidosService.obtenerUltimoPorPaciente(1L);

        assertNotNull(response);
        assertEquals(180, response.getColesterolTotal());
    }

    @Test
    void obtenerUltimoPorPaciente_NoEncontrado_LanzaResourceNotFoundException() {
        when(lipidosRepository.findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(99L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lipidosService.obtenerUltimoPorPaciente(99L));
    }

    @Test
    void eliminar_Encontrado_EliminaCorrectamente() {
        ControlSalud control = crearControl();
        Lipidos lipidos = crearLipidos(control);
        when(lipidosRepository.findById(1L)).thenReturn(Optional.of(lipidos));

        lipidosService.eliminar(1L);

        verify(lipidosRepository).delete(lipidos);
    }

    @Test
    void eliminar_NoEncontrado_LanzaResourceNotFoundException() {
        when(lipidosRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> lipidosService.eliminar(99L));
    }
}
