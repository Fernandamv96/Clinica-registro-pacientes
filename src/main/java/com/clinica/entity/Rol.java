package com.clinica.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Roles del sistema clínico.
 * Se modelan con enum para garantizar valores consistentes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "roles")
public class Rol extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true, length = 30)
    private RolNombre nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    public enum RolNombre {
        ADMIN,
        MEDICO,
        RECEPCIONISTA
    }
}
