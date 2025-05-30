package com.uds.project.service_authentification_compte.controller;

import com.uds.project.service_authentification_compte.configuration.JwtUtils;
import com.uds.project.service_authentification_compte.entity.RegisterDto;
import com.uds.project.service_authentification_compte.entity.Role;
import com.uds.project.service_authentification_compte.entity.User;
import com.uds.project.service_authentification_compte.repository.RoleRepository;
import com.uds.project.service_authentification_compte.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerRegisterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void testRegister_Success() throws Exception {
        String username = "newuser";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName("USER");

        when(userRepository.findByUsername(username)).thenReturn(null);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(mockRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(100L);
            return user;
        });

        String jsonRequest = """
            {
                "username": "newuser",
                "password": "password123",
                "roleNames": ["USER"]
            }
            """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.roleNames[0]").value("USER"));
    }
  @Test
void testRegister_Fail_UserAlreadyExists() throws Exception {
    String username = "existinguser";

    when(userRepository.findByUsername(username)).thenReturn(new User());

    String jsonRequest = """
        {
            "username": "existinguser",
            "password": "password123",
            "roleNames": ["USER"]
        }
        """;

    mockMvc.perform(post("/api/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Username already exists"));
}
@Test
void testRegister_Fail_RoleNotFound() throws Exception {
    String username = "newuser";

    when(userRepository.findByUsername(username)).thenReturn(null);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

    String jsonRequest = """
        {
            "username": "newuser",
            "password": "password123",
            "roleNames": ["INVALID_ROLE"]
        }
        """;

    mockMvc.perform(post("/api/user/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
            .andExpect(status().isInternalServerError());
}
}
