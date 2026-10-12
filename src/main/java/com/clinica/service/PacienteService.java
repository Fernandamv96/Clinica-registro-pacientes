package com.clinica.service;

import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PacienteService {
    Paciente create(PacienteRequest request);
    Paciente update(Long id, PacienteRequest request);
    void delete(Long id);
    Paciente findById(Long id);
    Page<Paciente> search(String rut, String nombre, String email, Pageable pageable);
}
