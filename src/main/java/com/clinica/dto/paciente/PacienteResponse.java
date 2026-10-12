package com.clinica.dto.paciente;

import com.clinica.entity.Paciente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteResponse {

    private Long id;
    private String rut;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private Paciente.Sexo sexo;
    private String direccion;
    private String telefono;
    private String email;
    private String historialMedicoBasico;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PacienteResponse from(Paciente p) {
        Integer edad = (p.getFechaNacimiento() != null)
                ? LocalDate.now().getYear() - p.getFechaNacimiento().getYear()
                : null;
        return PacienteResponse.builder()
                .id(p.getId())
                .rut(p.getRut())
                .nombreCompleto(p.getNombreCompleto())
                .fechaNacimiento(p.getFechaNacimiento())
                .edad(edad)
                .sexo(p.getSexo())
                .direccion(p.getDireccion())
                .telefono(p.getTelefono())
                .email(p.getEmail())
                .historialMedicoBasico(p.getHistorialMedicoBasico())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
