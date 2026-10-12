package com.clinica.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regresión: /auth/register no debe permitir que un usuario anónimo
 * (o sin rol ADMIN) se cree una cuenta con cualquier rol.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerSecurityTest {

    private static final String REGISTER_BODY = """
            {"username":"atacante","email":"a@example.com","password":"Atacante123!",
             "nombreCompleto":"X","roles":["ADMIN"]}
            """;

    @Autowired private MockMvc mvc;

    @Test
    void anonymousCannotRegister() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void nonAdminCannotRegister() throws Exception {
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY))
                .andExpect(status().isForbidden());
    }
}
