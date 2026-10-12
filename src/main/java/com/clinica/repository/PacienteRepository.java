package com.clinica.repository;

import com.clinica.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Paciente.
 * Usa Spring Data JPA + JPQL para queries específicas con búsqueda flexible.
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByRut(String rut);

    boolean existsByRut(String rut);

    boolean existsByEmail(String email);

    /**
     * Búsqueda flexible por RUT, nombre o email (case-insensitive, parcial).
     * Usa COALESCE para que, si los filtros son null, no filtre por ese campo.
     */
    @Query("""
            SELECT p FROM Paciente p
            WHERE (:rut IS NULL OR LOWER(p.rut) LIKE LOWER(CONCAT('%', :rut, '%')))
              AND (:nombre IS NULL OR LOWER(p.nombreCompleto) LIKE LOWER(CONCAT('%', :nombre, '%')))
              AND (:email IS NULL OR LOWER(p.email) LIKE LOWER(CONCAT('%', :email, '%')))
            """)
    Page<Paciente> search(
            @Param("rut") String rut,
            @Param("nombre") String nombre,
            @Param("email") String email,
            Pageable pageable
    );
}
