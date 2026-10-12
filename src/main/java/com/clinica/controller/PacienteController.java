package com.clinica.controller;

import com.clinica.dto.common.ApiResponse;
import com.clinica.dto.common.PageResponse;
import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.dto.paciente.PacienteResponse;
import com.clinica.entity.Paciente;
import com.clinica.service.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pacientes", description = "Gestión de pacientes del sistema clínico")
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    @Operation(summary = "Listar/buscar pacientes con paginación y filtros")
    public ResponseEntity<ApiResponse<PageResponse<PacienteResponse>>> list(
            @Parameter(description = "Filtrar por RUT (parcial)") @RequestParam(required = false) String rut,
            @Parameter(description = "Filtrar por nombre (parcial)") @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por email (parcial)") @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        Pageable pageable = buildPageable(page, size, sort);
        Page<Paciente> result = pacienteService.search(rut, nombre, email, pageable);
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(result.map(PacienteResponse::from))));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    @Operation(summary = "Obtener paciente por ID")
    public ResponseEntity<ApiResponse<PacienteResponse>> getById(@PathVariable Long id) {
        Paciente p = pacienteService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(PacienteResponse.from(p)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    @Operation(summary = "Crear nuevo paciente")
    public ResponseEntity<ApiResponse<PacienteResponse>> create(@Valid @RequestBody PacienteRequest req) {
        Paciente created = pacienteService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Paciente creado", PacienteResponse.from(created)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    @Operation(summary = "Actualizar paciente existente")
    public ResponseEntity<ApiResponse<PacienteResponse>> update(
            @PathVariable Long id, @Valid @RequestBody PacienteRequest req) {
        Paciente updated = pacienteService.update(id, req);
        return ResponseEntity.ok(ApiResponse.ok("Paciente actualizado", PacienteResponse.from(updated)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar paciente (solo ADMIN)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        pacienteService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente eliminado", null));
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction dir = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(dir, field));
    }
}
