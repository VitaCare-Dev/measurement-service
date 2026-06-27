package com.grupo10.measurement_service.service;

import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.repository.ControlSaludRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControlSaludServiceTest {

    @Mock
    private ControlSaludRepository controlSaludRepository;

    @InjectMocks
    private ControlSaludService controlSaludService;

    @Test
    void crearControl_IdPacienteNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> controlSaludService.crearControl(null, "nota"));
    }

    @Test
    void crearControl_DatosValidos_RetornaControlSalud() {
        ControlSalud control = new ControlSalud();
        control.setIdPaciente(1L);
        control.setFechaHora(LocalDateTime.now());
        control.setNotas("nota");
        when(controlSaludRepository.save(any(ControlSalud.class))).thenReturn(control);

        ControlSalud resultado = controlSaludService.crearControl(1L, "nota");

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdPaciente());
        verify(controlSaludRepository).save(any(ControlSalud.class));
    }

    @Test
    void obtenerHistorialPorPaciente_IdPacienteNulo_LanzaBusinessLogicException() {
        assertThrows(BusinessLogicException.class, () -> controlSaludService.obtenerHistorialPorPaciente(null));
    }

    @Test
    void obtenerHistorialPorPaciente_HistorialVacio_RetornaListaVacia() {
        when(controlSaludRepository.findByIdPacienteOrderByFechaHoraDesc(1L)).thenReturn(Collections.emptyList());

        List<ControlSalud> resultado = controlSaludService.obtenerHistorialPorPaciente(1L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerHistorialPorPaciente_ConRegistros_RetornaLista() {
        ControlSalud control = new ControlSalud();
        control.setIdPaciente(1L);
        when(controlSaludRepository.findByIdPacienteOrderByFechaHoraDesc(1L)).thenReturn(List.of(control));

        List<ControlSalud> resultado = controlSaludService.obtenerHistorialPorPaciente(1L);

        assertEquals(1, resultado.size());
    }
}
