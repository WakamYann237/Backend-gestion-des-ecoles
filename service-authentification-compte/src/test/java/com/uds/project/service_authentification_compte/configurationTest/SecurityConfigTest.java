package com.uds.project.service_authentification_compte.configurationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.uds.project.service_authentification_compte.entity.Role;
import com.uds.project.service_authentification_compte.entity.User;
import com.uds.project.service_authentification_compte.repository.RoleRepository;
import com.uds.project.service_authentification_compte.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testPasswordEncoderIsBCrypt() {
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
        String rawPassword = "test123";
        String encoded = passwordEncoder.encode(rawPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
    }

    @Test
    public void testAuthenticationManagerLoads() {
        assertNotNull(authenticationManager);
    }

    @Test
public void testLoginEndpointAccessibleWithoutAuth() throws Exception {
    Role userRole = roleRepository.findByName("ROLE_ADMIN")
        .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").description("role admin").build()));

    User user = User.builder()
        .username("xavi")
        .password(passwordEncoder.encode("admin"))
        .roles(Set.of(userRole))
        .build();

    userRepository.save(user);

    mockMvc.perform(post("/api/user/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"xavi\",\"password\":\"admin\"}"))
        .andExpect(status().isOk());
}
}
