package com.clinica.controller;

import com.clinica.dto.common.ApiResponse;
import com.clinica.dto.common.PageResponse;
import com.clinica.dto.historial.HistorialClinicoRequest;
import com.clinica.dto.historial.HistorialClinicoResponse;
import com.clinica.entity.HistorialClinico;
import com.clinica.service.HistorialClinicoService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/historial")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Historial Clínico", description = "Gestión de registros clínicos de pacientes")
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    @Operation(summary = "Crear nueva entrada en historial clínico")
    public ResponseEntity<ApiResponse<HistorialClinicoResponse>> create(@Valid @RequestBody HistorialClinicoRequest req) {
        HistorialClinico created = historialService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Entrada de historial creada",
                        HistorialClinicoResponse.from(created)));
    }

    @GetMapping("/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','RECEPCIONISTA')")
    @Operation(summary = "Listar historial clínico de un paciente (paginado, ordenado por fecha desc)")
    public ResponseEntity<ApiResponse<PageResponse<HistorialClinicoResponse>>> getByPaciente(
            @PathVariable Long pacienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<HistorialClinico> result = historialService.findByPaciente(pacienteId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(result.map(HistorialClinicoResponse::from))));
    }
}
