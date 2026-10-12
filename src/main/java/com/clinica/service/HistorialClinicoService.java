package com.clinica.service;

import com.clinica.dto.historial.HistorialClinicoRequest;
import com.clinica.entity.HistorialClinico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistorialClinicoService {
    HistorialClinico create(HistorialClinicoRequest req);
    Page<HistorialClinico> findByPaciente(Long pacienteId, Pageable pageable);
}
