package com.clinica.service.impl;

import com.clinica.dto.paciente.PacienteRequest;
import com.clinica.entity.Paciente;
import com.clinica.exception.DuplicateResourceException;
import com.clinica.exception.ResourceNotFoundException;
import com.clinica.repository.PacienteRepository;
import com.clinica.service.PacienteService;
import com.clinica.util.RutValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    @Override
    @Transactional
    public Paciente create(PacienteRequest req) {
        req.validateRut();
        String rutNormalizado = RutValidator.normalize(req.getRut());

        if (pacienteRepository.existsByRut(rutNormalizado)) {
            throw new DuplicateResourceException("Ya existe un paciente con RUT " + rutNormalizado);
        }
        if (req.getEmail() != null && pacienteRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Ya existe un paciente con email " + req.getEmail());
        }

        Paciente p = Paciente.builder()
                .rut(rutNormalizado)
                .nombreCompleto(req.getNombreCompleto())
                .fechaNacimiento(req.getFechaNacimiento())
                .sexo(req.getSexo())
                .direccion(req.getDireccion())
                .telefono(req.getTelefono())
                .email(req.getEmail())
                .historialMedicoBasico(req.getHistorialMedicoBasico())
                .build();

        Paciente saved = pacienteRepository.save(p);
        log.info("Paciente creado id={} rut={}", saved.getId(), saved.getRut());
        return saved;
    }

    @Override
    @Transactional
    public Paciente update(Long id, PacienteRequest req) {
        req.validateRut();
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", id));

        String rutNormalizado = RutValidator.normalize(req.getRut());
        if (!p.getRut().equals(rutNormalizado) && pacienteRepository.existsByRut(rutNormalizado)) {
            throw new DuplicateResourceException("Ya existe un paciente con RUT " + rutNormalizado);
        }
        if (req.getEmail() != null && !req.getEmail().equals(p.getEmail())
                && pacienteRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Ya existe un paciente con email " + req.getEmail());
        }

        p.setRut(rutNormalizado);
        p.setNombreCompleto(req.getNombreCompleto());
        p.setFechaNacimiento(req.getFechaNacimiento());
        p.setSexo(req.getSexo());
        p.setDireccion(req.getDireccion());
        p.setTelefono(req.getTelefono());
        p.setEmail(req.getEmail());
        p.setHistorialMedicoBasico(req.getHistorialMedicoBasico());

        log.info("Paciente actualizado id={}", p.getId());
        return p;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Paciente p = pacienteRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", id));
        pacienteRepository.delete(p);
        log.info("Paciente eliminado id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente findById(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Paciente", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Paciente> search(String rut, String nombre, String email, Pageable pageable) {
        // Permite pasar vacíos: los normalizamos a null para que el COALESCE no filtre
        return pacienteRepository.search(
                emptyToNull(rut),
                emptyToNull(nombre),
                emptyToNull(email),
                pageable
        );
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
