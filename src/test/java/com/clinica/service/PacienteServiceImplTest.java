package com.clinica.service;

import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.entity.Paciente;
import com.clinica.exception.DuplicateResourceException;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.repository.PacienteRepository;
import com.clinica.service.impl.PacienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock private PacienteRepository pacienteRepository;
    @InjectMocks private PacienteServiceImpl service;

    private PacienteRequest request;

    @BeforeEach
    void setUp() {
        request = PacienteRequest.builder()
                .rut("12345678-5")
                .nombreCompleto("Juan Pérez")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .sexo(Paciente.Sexo.MASCULINO)
                .email("juan@test.cl")
                .telefono("+56912345678")
                .direccion("Av. Siempre Viva 742")
                .build();
    }

    @Test
    void shouldCreatePaciente() {
        when(pacienteRepository.existsByRut(anyString())).thenReturn(false);
        when(pacienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class)))
                .thenAnswer(inv -> {
                    Paciente p = inv.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        Paciente result = service.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRut()).isEqualTo("123456785");
        assertThat(result.getNombreCompleto()).isEqualTo("Juan Pérez");
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    void shouldRejectDuplicateRut() {
        when(pacienteRepository.existsByRut(anyString())).thenReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("RUT");
    }

    @Test
    void shouldFindById() {
        Paciente p = Paciente.builder().id(1L).rut("123456785").nombreCompleto("Test").build();
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(p));

        Paciente result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDelete() {
        Paciente p = Paciente.builder().id(1L).build();
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(p));

        service.delete(1L);

        verify(pacienteRepository).delete(p);
    }
}
