package com.clinica.dto.historial;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialClinicoRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    private Long pacienteId;

    private Long medicoId; // opcional; si es null se toma del usuario autenticado

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fecha;

    @NotBlank(message = "El motivo de consulta es obligatorio")
    @Size(max = 300)
    private String motivoConsulta;

    private String diagnostico;

    private String tratamiento;

    private String observaciones;
}
