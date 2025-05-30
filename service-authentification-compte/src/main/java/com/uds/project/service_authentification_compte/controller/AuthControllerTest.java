package com.uds.project.service_authentification_compte.controller;

import com.uds.project.service_authentification_compte.configuration.JwtUtils;
import com.uds.project.service_authentification_compte.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private com.uds.project.service_authentification_compte.repository.UserRepository userRepository;

    @MockBean
    private com.uds.project.service_authentification_compte.repository.RoleRepository roleRepository;

    @Test
    void testLoginWithValidCredentials() throws Exception {
        // Préparation des données
        String username = "admin";
        String password = "password";
        String jsonRequest = """
            {
                "username": "admin",
                "password": "password"
            }
            """;

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtUtils.generateToken(username)).thenReturn("fake-jwt-token");

        // Appel de la méthode via MockMvc
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }
    @Test
void testLoginWithInvalidCredentials() throws Exception {
    // Préparation des données
    String jsonRequest = """
        {
            "username": "admin",
            "password": "wrongpassword"
        }
        """;

    // Simuler une exception d'authentification
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new org.springframework.security.core.AuthenticationException("Bad credentials") {});

    // Exécution de la requête POST /api/user/login
    mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string("Invalid Username or Password"));
}

}
