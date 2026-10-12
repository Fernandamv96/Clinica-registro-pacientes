package com.clinica.service.impl;

import com.clinica.dto.historial.HistorialClinicoRequest;
import com.clinica.entity.HistorialClinico;
import com.clinica.entity.Paciente;
import com.clinica.entity.Usuario;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.repository.HistorialClinicoRepository;
import com.clinica.repository.PacienteRepository;
import com.clinica.repository.UsuarioRepository;
import com.clinica.service.HistorialClinicoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public HistorialClinico create(HistorialClinicoRequest req) {
        Paciente paciente = pacienteRepository.findById(req.getPacienteId())
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", req.getPacienteId()));

        Usuario medico = null;
        if (req.getMedicoId() != null) {
            medico = usuarioRepository.findById(req.getMedicoId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Médico (Usuario)", req.getMedicoId()));
        }

        HistorialClinico h = HistorialClinico.builder()
                .paciente(paciente)
                .medico(medico)
                .fecha(req.getFecha())
                .motivoConsulta(req.getMotivoConsulta())
                .diagnostico(req.getDiagnostico())
                .tratamiento(req.getTratamiento())
                .observaciones(req.getObservaciones())
                .build();

        HistorialClinico saved = historialRepository.save(h);
        log.info("Historial clínico creado id={} pacienteId={}", saved.getId(), paciente.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistorialClinico> findByPaciente(Long pacienteId, Pageable pageable) {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw ResourceNotFoundException.of("Paciente", pacienteId);
        }
        return historialRepository.findByPacienteIdOrderByFechaDesc(pacienteId, pageable);
    }
}
