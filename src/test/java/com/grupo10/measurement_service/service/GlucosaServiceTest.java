package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.GlucosaRequestDto;
import com.grupo10.measurement_service.dto.GlucosaResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.Glucosa;
import com.grupo10.measurement_service.model.PeriodoGlucosa;
import com.grupo10.measurement_service.repository.GlucosaRepository;
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
    void obtenerHistorialPorPaciente_IdNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> glucosaService.obtenerHistorialPorPaciente(null));
    }

    @Test
    void obtenerHistorialPorPaciente_ListaVacia_RetornaVacia() {
        when(glucosaRepository.findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(Collections.emptyList());

        List<GlucosaResponseDto> resultado = glucosaService.obtenerHistorialPorPaciente(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerHistorialPorPaciente_ConRegistros_RetornaLista() {
        ControlSalud control = crearControl();
        Glucosa glucosa = crearGlucosa(control);
        when(glucosaRepository.findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(List.of(glucosa));

        List<GlucosaResponseDto> resultado = glucosaService.obtenerHistorialPorPaciente(1L);

        assertEquals(1, resultado.size());
        assertEquals(95, resultado.get(0).getGlucosa());
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
