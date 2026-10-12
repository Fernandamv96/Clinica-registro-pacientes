package com.clinica.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Paciente del sistema clínico.
 * - RUT validado (almacenado normalizado sin puntos ni guión, con DV).
 * - OneToMany con HistorialClinico (carga LAZY para optimizar lecturas masivas).
 * - Índices en campos consultados frecuentemente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "pacientes",
        indexes = {
                @Index(name = "idx_paciente_rut", columnList = "rut", unique = true),
                @Index(name = "idx_paciente_email", columnList = "email"),
                @Index(name = "idx_paciente_nombre", columnList = "nombre_completo")
        }
)
public class Paciente extends BaseEntity {

    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, length = 15)
    private Sexo sexo;

    @Column(name = "direccion", length = 250)
    private String direccion;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "historial_medico_basico", columnDefinition = "TEXT")
    private String historialMedicoBasico;

    @OneToMany(
            mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<HistorialClinico> historiales = new ArrayList<>();

    public enum Sexo {
        MASCULINO,
        FEMENINO,
        OTRO
    }

    /** Helper para mantener la relación bidireccional consistente. */
    public void addHistorial(HistorialClinico historial) {
        historiales.add(historial);
        historial.setPaciente(this);
    }

    public void removeHistorial(HistorialClinico historial) {
        historiales.remove(historial);
        historial.setPaciente(null);
    }
}
