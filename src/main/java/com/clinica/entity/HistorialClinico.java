package com.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entrada del historial clínico de un paciente (consulta, diagnóstico, tratamiento).
 * Relación ManyToOne con Paciente (un paciente -> muchos registros).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "historial_clinico",
        indexes = {
                @Index(name = "idx_historial_paciente", columnList = "paciente_id"),
                @Index(name = "idx_historial_fecha", columnList = "fecha")
        }
)
public class HistorialClinico extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false,
            foreignKey = @jakarta.persistence.ForeignKey(name = "fk_historial_paciente"))
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id",
            foreignKey = @jakarta.persistence.ForeignKey(name = "fk_historial_medico"))
    private Usuario medico;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "motivo_consulta", nullable = false, length = 300)
    private String motivoConsulta;

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "tratamiento", columnDefinition = "TEXT")
    private String tratamiento;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
