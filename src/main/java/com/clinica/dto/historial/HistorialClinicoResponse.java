package com.clinica.dto.historial;

import com.clinica.entity.HistorialClinico;
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
public class HistorialClinicoResponse {

    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private Long medicoId;
    private String medicoNombre;
    private LocalDate fecha;
    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private LocalDateTime createdAt;

    public static HistorialClinicoResponse from(HistorialClinico h) {
        return HistorialClinicoResponse.builder()
                .id(h.getId())
                .pacienteId(h.getPaciente() != null ? h.getPaciente().getId() : null)
                .pacienteNombre(h.getPaciente() != null ? h.getPaciente().getNombreCompleto() : null)
                .medicoId(h.getMedico() != null ? h.getMedico().getId() : null)
                .medicoNombre(h.getMedico() != null ? h.getMedico().getNombreCompleto() : null)
                .fecha(h.getFecha())
                .motivoConsulta(h.getMotivoConsulta())
                .diagnostico(h.getDiagnostico())
                .tratamiento(h.getTratamiento())
                .observaciones(h.getObservaciones())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
