package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.dto.MedicionVitalRequestDto;
import com.grupo10.measurement_service.dto.MedicionVitalResponseDto;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.exception.ResourceNotFoundException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.model.MedicionVitales;
import com.grupo10.measurement_service.repository.MedicionVitalesRepository;
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
    void obtenerHistorialPorPaciente_IdNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> medicionVitalService.obtenerHistorialPorPaciente(null));
    }

    @Test
    void obtenerHistorialPorPaciente_ListaVacia_RetornaVacia() {
        when(medicionVitalesRepository.findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(Collections.emptyList());

        List<MedicionVitalResponseDto> resultado = medicionVitalService.obtenerHistorialPorPaciente(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerHistorialPorPaciente_ConRegistros_RetornaLista() {
        ControlSalud control = crearControl();
        MedicionVitales vital = crearVital(control);
        when(medicionVitalesRepository.findByControlSalud_IdPacienteOrderByControlSalud_FechaHoraDesc(1L))
                .thenReturn(List.of(vital));

        List<MedicionVitalResponseDto> resultado = medicionVitalService.obtenerHistorialPorPaciente(1L);

        assertEquals(1, resultado.size());
        assertEquals(120, resultado.get(0).getPresionSistolica());
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
