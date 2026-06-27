package com.grupo10.measurement_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MeasurementServiceApplicationTests {

    @Test
    void constructor_InstanciaClaseCorrectamente() {
        MeasurementServiceApplication app = new MeasurementServiceApplication();
        assertNotNull(app);
    }

    @Test
    void main_IniciandoAplicacionSpring() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            MeasurementServiceApplication.main(new String[]{});
            mockedSpringApplication.verify(() -> SpringApplication.run(MeasurementServiceApplication.class, new String[]{}));
        }
    }
}
