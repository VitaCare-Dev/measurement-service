package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.MedicionVitalRequestDto;
import com.grupo10.measurement_service.dto.MedicionVitalResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.MedicionVitales;
import com.grupo10.measurement_service.dto.PageResponseDto;
import com.grupo10.measurement_service.repository.MedicionVitalesRepository;
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
class MedicionVitalServiceTest {

    @Mock
    private MedicionVitalesRepository medicionVitalesRepository;

    @Mock
    private ControlSaludService controlSaludService;

    @InjectMocks
    private MedicionVitalService medicionVitalService;

    private ControlSalud crearControl() {
        ControlSalud control = new ControlSalud();
        control.setIdControl(1L);
        control.setIdPaciente(1L);
        control.setFechaHora(LocalDateTime.now());
        control.setNotas("nota");
        return control;
    }

    private MedicionVitales crearVital(ControlSalud control) {
        MedicionVitales vital = new MedicionVitales();
        vital.setIdControl(1L);
        vital.setPresionSistolica(120);
        vital.setPresionDiastolica(80);
        vital.setTemperatura(36.5);
        vital.setPeso(72.3);
        vital.setControlSalud(control);
        return vital;
    }

    private MedicionVitalRequestDto crearRequest() {
        MedicionVitalRequestDto request = new MedicionVitalRequestDto();
        request.setIdPaciente(1L);
        request.setNotas("nota");
        request.setPresionSistolica(120);
        request.setPresionDiastolica(80);
        request.setTemperatura(36.5);
        request.setPeso(72.3);
        return request;
    }

    @Test
    void registrarMedicionVital_PresionSistolicaNula_LanzaBusinessLogicException() {
        MedicionVitalRequestDto request = crearRequest();
        request.setPresionSistolica(null);
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.registrarMedicionVital(request));
    }

    @Test
    void registrarMedicionVital_PresionSistolicaMenorOIgualCero_LanzaBusinessLogicException() {
        MedicionVitalRequestDto request = crearRequest();
        request.setPresionSistolica(0);
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.registrarMedicionVital(request));
    }

    @Test
    void registrarMedicionVital_PresionDiastolicaNula_LanzaBusinessLogicException() {
        MedicionVitalRequestDto request = crearRequest();
        request.setPresionDiastolica(null);
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.registrarMedicionVital(request));
    }

    @Test
    void registrarMedicionVital_PresionDiastolicaMenorOIgualCero_LanzaBusinessLogicException() {
        MedicionVitalRequestDto request = crearRequest();
        request.setPresionDiastolica(0);
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.registrarMedicionVital(request));
    }

    @Test
    void registrarMedicionVital_TemperaturaMenorOIgualCero_LanzaBusinessLogicException() {
        MedicionVitalRequestDto request = crearRequest();
        request.setTemperatura(0);
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.registrarMedicionVital(request));
    }

    @Test
    void registrarMedicionVital_PesoMenorOIgualCero_LanzaBusinessLogicException() {
        MedicionVitalRequestDto request = crearRequest();
        request.setPeso(0);
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.registrarMedicionVital(request));
    }

    @Test
    void registrarMedicionVital_DatosValidos_RetornaResponse() {
        MedicionVitalRequestDto request = crearRequest();
        ControlSalud control = crearControl();
        MedicionVitales vital = crearVital(control);

        when(controlSaludService.crearControl(1L, "nota")).thenReturn(control);
        when(medicionVitalesRepository.save(any(MedicionVitales.class))).thenReturn(vital);

        MedicionVitalResponseDto response = medicionVitalService.registrarMedicionVital(request);

        assertNotNull(response);
        assertEquals(120, response.getPresionSistolica());
        assertEquals(1L, response.getIdPaciente());
        verify(medicionVitalesRepository).save(any(MedicionVitales.class));
    }

    @Test
    void obtenerPorId_Encontrado_RetornaResponse() {
        ControlSalud control = crearControl();
        MedicionVitales vital = crearVital(control);
        when(medicionVitalesRepository.findById(1L)).thenReturn(Optional.of(vital));

        MedicionVitalResponseDto response = medicionVitalService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getIdControl());
    }

    @Test
    void obtenerPorId_NoEncontrado_LanzaResourceNotFoundException() {
        when(medicionVitalesRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> medicionVitalService.obtenerPorId(99L));
    }

    @Test
    void obtenerHistorialPaginado_IdNulo_LanzaBusinessLogicException() {
        Pageable pageable = PageRequest.of(0, 10);
        assertThrows(BusinessLogicException.class,
                () -> medicionVitalService.obtenerHistorialPaginado(null, null, null, pageable));
    }

    @Test
    void obtenerHistorialPaginado_SinRegistros_RetornaPaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        when(medicionVitalesRepository.buscarHistorialPaginado(1L, null, null, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        PageResponseDto<MedicionVitalResponseDto> resultado =
                medicionVitalService.obtenerHistorialPaginado(1L, null, null, pageable);

        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());
    }

    @Test
    void obtenerHistorialPaginado_ConRegistros_RetornaPaginaConContenido() {
        ControlSalud control = crearControl();
        MedicionVitales vital = crearVital(control);
        Pageable pageable = PageRequest.of(0, 10);
        when(medicionVitalesRepository.buscarHistorialPaginado(1L, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(vital), pageable, 1));

        PageResponseDto<MedicionVitalResponseDto> resultado =
                medicionVitalService.obtenerHistorialPaginado(1L, null, null, pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals(120, resultado.getContent().get(0).getPresionSistolica());
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    void obtenerHistorialPaginado_ConRangoDeFechas_PropagaLosLimites() {
        LocalDateTime desde = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2026, 1, 31, 23, 59);
        Pageable pageable = PageRequest.of(0, 10);
        when(medicionVitalesRepository.buscarHistorialPaginado(1L, desde, hasta, pageable))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        medicionVitalService.obtenerHistorialPaginado(1L, desde, hasta, pageable);

        verify(medicionVitalesRepository).buscarHistorialPaginado(1L, desde, hasta, pageable);
    }

    @Test
    void obtenerUltimoPorPaciente_IdNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.obtenerUltimoPorPaciente(null));
    }

    @Test
    void obtenerUltimoPorPaciente_Encontrado_RetornaResponse() {
        ControlSalud control = crearControl();
        MedicionVitales vital = crearVital(control);
        when(medicionVitalesRepository.findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(Optional.of(vital));

        MedicionVitalResponseDto response = medicionVitalService.obtenerUltimoPorPaciente(1L);

        assertNotNull(response);
        assertEquals(120, response.getPresionSistolica());
    }

    @Test
    void obtenerUltimoPorPaciente_NoEncontrado_LanzaResourceNotFoundException() {
        when(medicionVitalesRepository.findTopByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(99L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> medicionVitalService.obtenerUltimoPorPaciente(99L));
    }

    @Test
    void eliminar_Encontrado_EliminaCorrectamente() {
        ControlSalud control = crearControl();
        MedicionVitales vital = crearVital(control);
        when(medicionVitalesRepository.findById(1L)).thenReturn(Optional.of(vital));

        medicionVitalService.eliminar(1L);

        verify(medicionVitalesRepository).delete(vital);
    }

    @Test
    void eliminar_NoEncontrado_LanzaResourceNotFoundException() {
        when(medicionVitalesRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> medicionVitalService.eliminar(99L));
    }
}
