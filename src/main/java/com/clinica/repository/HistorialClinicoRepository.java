package com.clinica.repository;

import com.clinica.entity.HistorialClinico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {

    Page<HistorialClinico> findByPacienteIdOrderByFechaDesc(Long pacienteId, Pageable pageable);
}
