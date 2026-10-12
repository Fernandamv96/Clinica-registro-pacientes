package com.clinica.security;

import com.clinica.entity.Rol;
import com.clinica.entity.Usuario;
import com.clinica.repository.RolRepository;
import com.clinica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Inicializador de datos: crea los roles base y un usuario admin si no existen.
 * Útil para evitar que el sistema arranque sin un superusuario.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // 1) Crear roles base
        for (Rol.RolNombre rn : Rol.RolNombre.values()) {
            if (rolRepository.findByNombre(rn).isEmpty()) {
                rolRepository.save(Rol.builder()
                        .nombre(rn)
                        .descripcion("Rol " + rn.name())
                        .build());
                log.info("Rol creado: {}", rn);
            }
        }

        // 2) Crear admin por defecto
        if (!usuarioRepository.existsByUsername("admin")) {
            Rol adminRol = rolRepository.findByNombre(Rol.RolNombre.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("Rol ADMIN no existe"));
            Usuario admin = Usuario.builder()
                    .username("admin")
                    .email("admin@clinica.cl")
                    .password(passwordEncoder.encode("***REMOVED***"))
                    .nombreCompleto("Administrador del Sistema")
                    .activo(true)
                    .roles(Set.of(adminRol))
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuario admin creado (password: ***REMOVED***)");
        }
    }
}
