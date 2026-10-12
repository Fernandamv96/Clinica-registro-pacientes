package com.clinica.service;

import com.clinica.dto.auth.AuthResponse;
import com.clinica.dto.auth.LoginRequest;
import com.clinica.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
