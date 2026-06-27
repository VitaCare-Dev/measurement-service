package com.grupo10.measurement_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grupo10.measurement_service.dto.GlucosaRequestDto;
import com.grupo10.measurement_service.dto.GlucosaResponseDto;
import com.grupo10.measurement_service.model.PeriodoGlucosa;
import com.grupo10.measurement_service.service.GlucosaService;
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
class GlucosaControllerTest {

    @Mock
    private GlucosaService glucosaService;

    @InjectMocks
    private GlucosaController glucosaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(glucosaController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private GlucosaResponseDto crearResponse() {
        GlucosaResponseDto response = new GlucosaResponseDto();
        response.setIdControl(1L);
        response.setIdPaciente(1L);
        response.setGlucosa(95);
        response.setPeriodo(PeriodoGlucosa.AYUNAS);
        response.setFechaHora(LocalDateTime.now());
        response.setNotas("nota");
        return response;
    }

    @Test
    void registrarGlucosa_RetornaCreated() throws Exception {
        GlucosaRequestDto request = new GlucosaRequestDto();
        request.setIdPaciente(1L);
        request.setGlucosa(95);
        request.setPeriodo(PeriodoGlucosa.AYUNAS);

        when(glucosaService.registrarGlucosa(any(GlucosaRequestDto.class))).thenReturn(crearResponse());

        mockMvc.perform(post("/api/glucose")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(glucosaService).registrarGlucosa(any(GlucosaRequestDto.class));
    }

    @Test
    void obtenerPorId_RetornaOk() throws Exception {
        when(glucosaService.obtenerPorId(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/glucose/1"))
                .andExpect(status().isOk());

        verify(glucosaService).obtenerPorId(1L);
    }

    @Test
    void obtenerHistorialPorPaciente_RetornaOk() throws Exception {
        when(glucosaService.obtenerHistorialPorPaciente(1L)).thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/glucose/patient/1"))
                .andExpect(status().isOk());

        verify(glucosaService).obtenerHistorialPorPaciente(1L);
    }

    @Test
    void obtenerUltimoPorPaciente_RetornaOk() throws Exception {
        when(glucosaService.obtenerUltimoPorPaciente(1L)).thenReturn(crearResponse());

        mockMvc.perform(get("/api/glucose/patient/1/latest"))
                .andExpect(status().isOk());

        verify(glucosaService).obtenerUltimoPorPaciente(1L);
    }

    @Test
    void eliminar_RetornaNoContent() throws Exception {
        doNothing().when(glucosaService).eliminar(1L);

        mockMvc.perform(delete("/api/glucose/1"))
                .andExpect(status().isNoContent());

        verify(glucosaService).eliminar(1L);
    }
}
