package com.clinica.service.impl;

import com.clinica.dto.auth.AuthResponse;
import com.clinica.dto.auth.LoginRequest;
import com.clinica.dto.auth.RegisterRequest;
import com.clinica.entity.Rol;
import com.clinica.entity.Usuario;
import com.clinica.exception.BusinessException;
import com.clinica.exception.DuplicateResourceException;
import com.clinica.repository.RolRepository;
import com.clinica.repository.UsuarioRepository;
import com.clinica.security.CustomUserDetails;
import com.clinica.security.JwtService;
import com.clinica.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (usuarioRepository.existsByUsername(req.getUsername())) {
            throw new DuplicateResourceException("Username ya en uso: " + req.getUsername());
        }
        if (usuarioRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateResourceException("Email ya en uso: " + req.getEmail());
        }

        Set<Rol> roles = new HashSet<>();
        for (Rol.RolNombre rn : req.getRoles()) {
            Rol rol = rolRepository.findByNombre(rn)
                    .orElseThrow(() -> new BusinessException("Rol no encontrado: " + rn));
            roles.add(rol);
        }

        Usuario user = Usuario.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .nombreCompleto(req.getNombreCompleto())
                .activo(true)
                .roles(roles)
                .build();

        usuarioRepository.save(user);
        log.info("Usuario registrado: {}", user.getUsername());

        return buildAuthResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        return buildAuthResponse(usuario);
    }

    private AuthResponse buildAuthResponse(Usuario usuario) {
        CustomUserDetails details = new CustomUserDetails(usuario);
        String token = jwtService.generateToken(details);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresInSeconds(jwtService.getExpirationSeconds())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .nombreCompleto(usuario.getNombreCompleto())
                .roles(usuario.getRoles().stream()
                        .map(r -> r.getNombre().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}
