package com.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal del Sistema de Registro de Pacientes.
 * Habilita la auditoría JPA para createdAt/updatedAt.
 */
@SpringBootApplication
@EnableJpaAuditing
public class ClinicaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicaApplication.class, args);
    }
}
