package com.clinica.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class RutValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678-5", "12.345.678-5", "123456785",
            "11111111-1", "22222222-2", "7639449-3"
    })
    void shouldAcceptValidRuts(String rut) {
        assertTrue(RutValidator.isValid(rut), "RUT esperado válido: " + rut);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345678-0", "11111111-2", "abcdefgh-1", "1234", "", " "
    })
    void shouldRejectInvalidRuts(String rut) {
        assertFalse(RutValidator.isValid(rut), "RUT esperado inválido: " + rut);
    }

    @Test
    void shouldNormalize() {
        assertEquals("123456785", RutValidator.normalize("12.345.678-5"));
        assertEquals("12345678K", RutValidator.normalize("12.345.678-k"));
    }

    @Test
    void shouldFormatValidRut() {
        assertEquals("12.345.678-5", RutValidator.format("12345678-5"));
    }

    @Test
    void shouldCalculateDV() {
        assertEquals('5', RutValidator.calcularDV(12345678));
        assertEquals('K', RutValidator.calcularDV(1));
    }
}
