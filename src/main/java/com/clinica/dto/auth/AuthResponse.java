package com.clinica.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type;
    private Long expiresInSeconds;
    private String username;
    private String email;
    private String nombreCompleto;
    private Set<String> roles;
}
