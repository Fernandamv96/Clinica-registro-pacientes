package com.clinica.dto.paciente;

import com.clinica.entity.Paciente;
import com.clinica.util.RutValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
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
public class PacienteRequest {

    @NotBlank(message = "El RUT es obligatorio")
    @Size(max = 12, message = "RUT demasiado largo")
    private String rut;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombreCompleto;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El sexo es obligatorio")
    private Paciente.Sexo sexo;

    @Size(max = 250, message = "La dirección no puede superar los 250 caracteres")
    private String direccion;

    @Pattern(regexp = "^[+0-9 ()-]{6,20}$", message = "Formato de teléfono inválido")
    private String telefono;

    @Email(message = "Debe ser un email válido")
    @Size(max = 120)
    private String email;

    private String historialMedicoBasico;

    /**
     * Validador semántico a nivel DTO. Se ejecuta manualmente en el servicio
     * para entregar errores de dominio limpios (no de parsing).
     */
    public void validateRut() {
        if (rut != null && !RutValidator.isValid(rut)) {
            throw new IllegalArgumentException("RUT inválido: " + rut);
        }
    }
}
