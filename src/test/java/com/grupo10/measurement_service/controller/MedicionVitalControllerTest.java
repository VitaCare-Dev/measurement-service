package com.grupo10.measurement_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grupo10.measurement_service.dto.MedicionVitalRequestDto;
import com.grupo10.measurement_service.dto.MedicionVitalResponseDto;
import com.grupo10.measurement_service.service.MedicionVitalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MedicionVitalControllerTest {

    @Mock
    private MedicionVitalService medicionVitalService;

    @InjectMocks
    private MedicionVitalController medicionVitalController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(medicionVitalController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private MedicionVitalResponseDto crearResponse() {
        MedicionVitalResponseDto response = new MedicionVitalResponseDto();
        response.setIdControl(1L);
        response.setIdPaciente(1L);
        response.setPresionSistolica(120);
        response.setPresionDiastolica(80);
        response.setTemperatura(36.5);
        response.setPeso(72.3);
        response.setFechaHora(LocalDateTime.now());
        response.setNotas("nota");
        return response;
    }

    @Test
    void registrarMedicionVital_RetornaCreated() throws Exception {
        MedicionVitalRequestDto request = new MedicionVitalRequestDto();
        request.setIdPaciente(1L);
        request.setPresionSistolica(120);
        request.setPresionDiastolica(80);
        request.setTemperatura(36.5);
        request.setPeso(72.3);

        when(medicionVitalService.registrarMedicionVital(any(MedicionVitalRequestDto.class))).thenReturn(crearResponse());

        mockMvc.perform(post("/api/vitals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(medicionVitalService).registrarMedicionVital(any(MedicionVitalRequestDto.class));
    }

    @Test
    void obtenerPorId_RetornaOk() throws Exception {
        when(medicionVitalService.obtenerPorId(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/vitals/1"))
                .andExpect(status().isOk());

        verify(medicionVitalService).obtenerPorId(1L);
    }

    @Test
    void obtenerHistorialPorPaciente_RetornaOk() throws Exception {
        when(medicionVitalService.obtenerHistorialPorPaciente(1L)).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/vitals/patient/1"))
                .andExpect(status().isOk());

        verify(medicionVitalService).obtenerHistorialPorPaciente(1L);
    }

    @Test
    void obtenerUltimoPorPaciente_RetornaOk() throws Exception {
        when(medicionVitalService.obtenerUltimoPorPaciente(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/vitals/patient/1/latest"))
                .andExpect(status().isOk());

        verify(medicionVitalService).obtenerUltimoPorPaciente(1L);
    }

    @Test
    void eliminar_RetornaNoContent() throws Exception {
        doNothing().when(medicionVitalService).eliminar(1L);

        mockMvc.perform(delete("/api/vitals/1"))
                .andExpect(status().isNoContent());

        verify(medicionVitalService).eliminar(1L);
    }
}
