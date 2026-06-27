package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Lipidos;
import com.grupo10.measurement_service.repository.LipidosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void obtenerHistorialPorPaciente_IdNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> lipidosService.obtenerHistorialPorPaciente(null));
    }

    @Test
    void obtenerHistorialPorPaciente_ListaVacia_RetornaVacia() {
        when(lipidosRepository.findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(Collections.emptyList());

        List<LipidosResponseDto> resultado = lipidosService.obtenerHistorialPorPaciente(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerHistorialPorPaciente_ConRegistros_RetornaLista() {
        ControlSalud control = crearControl();
        Lipidos lipidos = crearLipidos(control);
        when(lipidosRepository.findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(List.of(lipidos));

        List<LipidosResponseDto> resultado = lipidosService.obtenerHistorialPorPaciente(1L);

        assertEquals(1, resultado.size());
        assertEquals(180, resultado.get(0).getColesterolTotal());
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
