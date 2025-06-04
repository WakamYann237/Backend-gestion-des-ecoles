package com.uds.project.service_authentification_compte.configurationTest;


import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import com.uds.project.service_authentification_compte.configuration.JwtUtils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void testGenerateTokenAndExtractUsername() {
        String username = "testUser";
        String token = jwtUtils.generateToken(username);
        assertNotNull(token);

        String extractedUsername = jwtUtils.extraUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testValidateToken() {
        String username = "testUser";
        String token = jwtUtils.generateToken(username);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            username, "", new ArrayList<>()
        );

        assertTrue(jwtUtils.validateToken(token, userDetails));
    }

    @Test
    public void testExpiredToken() throws InterruptedException {
        JwtUtils jwtUtils = new JwtUtils();

        // Injection des champs privés
        setField(jwtUtils, "secretKey", "test_secret35577888890065543225667890928764432111234456");
        setField(jwtUtils, "expirationTime", 1L);

        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        Thread.sleep(10); // Assurer l’expiration

        UserDetails userDetails = User.withUsername(username).password("").authorities(Set.of()).build();
        assertFalse(jwtUtils.validateToken(token, userDetails));
    }

    // Méthode utilitaire pour injecter des valeurs dans des champs privés
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
