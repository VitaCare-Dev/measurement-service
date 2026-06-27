package com.grupo10.measurement_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grupo10.measurement_service.dto.LipidosRequestDto;
import com.grupo10.measurement_service.dto.LipidosResponseDto;
import com.grupo10.measurement_service.service.LipidosService;
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
class LipidosControllerTest {

    @Mock
    private LipidosService lipidosService;

    @InjectMocks
    private LipidosController lipidosController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(lipidosController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private LipidosResponseDto crearResponse() {
        LipidosResponseDto response = new LipidosResponseDto();
        response.setIdControl(1L);
        response.setIdPaciente(1L);
        response.setColesterolTotal(180);
        response.setColesterolLDL(110);
        response.setColesterolHDL(55);
        response.setTrigliceridos(130);
        response.setFechaHora(LocalDateTime.now());
        response.setNotas("nota");
        return response;
    }

    @Test
    void registrarLipidos_RetornaCreated() throws Exception {
        LipidosRequestDto request = new LipidosRequestDto();
        request.setIdPaciente(1L);
        request.setColesterolTotal(180);
        request.setColesterolLDL(110);
        request.setColesterolHDL(55);
        request.setTrigliceridos(130);

        when(lipidosService.registrarLipidos(any(LipidosRequestDto.class))).thenReturn(crearResponse());

        mockMvc.perform(post("/api/lipids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(lipidosService).registrarLipidos(any(LipidosRequestDto.class));
    }

    @Test
    void obtenerPorId_RetornaOk() throws Exception {
        when(lipidosService.obtenerPorId(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/lipids/1"))
                .andExpect(status().isOk());

        verify(lipidosService).obtenerPorId(1L);
    }

    @Test
    void obtenerHistorialPorPaciente_RetornaOk() throws Exception {
        when(lipidosService.obtenerHistorialPorPaciente(1L)).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/lipids/patient/1"))
                .andExpect(status().isOk());

        verify(lipidosService).obtenerHistorialPorPaciente(1L);
    }

    @Test
    void obtenerUltimoPorPaciente_RetornaOk() throws Exception {
        when(lipidosService.obtenerUltimoPorPaciente(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/lipids/patient/1/latest"))
                .andExpect(status().isOk());

        verify(lipidosService).obtenerUltimoPorPaciente(1L);
    }

    @Test
    void eliminar_RetornaNoContent() throws Exception {
        doNothing().when(lipidosService).eliminar(1L);

        mockMvc.perform(delete("/api/lipids/1"))
                .andExpect(status().isNoContent());

        verify(lipidosService).eliminar(1L);
    }
}
