package com.uds.project.service_authentification_compte.test_authcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uds.project.service_authentification_compte.entity.RegisterDto;
import com.uds.project.service_authentification_compte.entity.User;
import com.uds.project.service_authentification_compte.repository.UserRepository;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

      @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets en JSON

    @Test
    public void testLoginMissingUsername() throws Exception {
        String json = "{\"password\": \"testpass\"}";

        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username and password are required."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers_Authorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

@Test
@WithMockUser(roles = "ADMIN")
public void testRegisterUser() throws Exception {
    // Assure-toi que le rôle existe bien dans la BDD avec ce nom exact ("ROLE_PROFESSEUR")
    RegisterDto dto = new RegisterDto("borel", "password1273", Set.of("ROLE_PROFESSEUR"));
    String json = objectMapper.writeValueAsString(dto);

    mockMvc.perform(post("/api/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andDo(print())  // affiche la requête et la réponse dans la console
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("borel"))
        .andExpect(jsonPath("$.roles").isArray())
        .andExpect(jsonPath("$.roles").value(org.hamcrest.Matchers.hasItem("ROLE_PROFESSEUR")));
}



@Test
@WithMockUser(roles = "ADMIN")
public void testUpdateUser_ShouldSucceed() throws Exception {
    // 1. Créer un utilisateur initial
    RegisterDto originalUser = new RegisterDto("bobo", "originalPass", Set.of("ROLE_PROFESSEUR"));
    String createJson = objectMapper.writeValueAsString(originalUser);

    mockMvc.perform(post("/api/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson))
        .andExpect(status().isOk());

    // 2. Récupérer l'utilisateur depuis la BDD pour obtenir son ID
    User createdUser = userRepository.findByUsername("bobo");
    Long userId = createdUser.getId();
    assertNotNull(userId);

    // 3. Créer un DTO de mise à jour
    RegisterDto updatedUser = new RegisterDto("updated_user", "newPassword123", Set.of("ROLE_ADMIN"));
    String updateJson = objectMapper.writeValueAsString(updatedUser);

    // 4. Exécuter la requête PUT avec affichage de la réponse
    mockMvc.perform(put("/api/user/" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateJson))
        .andDo(print()) // Affiche la réponse HTTP complète pour débogage
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("updated_user"))
        .andExpect(jsonPath("$.roleNames").isArray())
        .andExpect(jsonPath("$.roleNames").value(org.hamcrest.Matchers.contains("ROLE_ADMIN")));

    // 5. Vérification en base de données
    User modifiedUser = userRepository.findById(userId).orElseThrow();
    assertEquals("updated_user", modifiedUser.getUsername());
    assertTrue(passwordEncoder.matches("newPassword123", modifiedUser.getPassword()));
}


@Test
@WithMockUser(username = "admin", roles = "ADMIN")
public void testDeleteUser() throws Exception {
    // Étape 1 : Créer un utilisateur à supprimer
    RegisterDto userToDelete = new RegisterDto("delete_test_user", "pass123", Set.of("USER"));
    String createJson = objectMapper.writeValueAsString(userToDelete);

    mockMvc.perform(post("/api/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createJson))
        .andExpect(status().isOk());

    Long userId = userRepository.findByUsername("delete_test_user").getId();

    // Étape 2 : Supprimer l'utilisateur
    mockMvc.perform(delete("/api/user/delete/" + userId))
        .andExpect(status().isOk())
        .andExpect(content().string("Utilisateur supprimé avec succès."));
}

}
