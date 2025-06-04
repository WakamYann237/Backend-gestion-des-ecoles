package com.uds.project.service_authentification_compte.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import com.uds.project.service_authentification_compte.entity.Role;
import com.uds.project.service_authentification_compte.entity.User;
import com.uds.project.service_authentification_compte.repository.UserRepository;
import com.uds.project.service_authentification_compte.service.UserService;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;  // la classe à tester

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserFound() {
        User user = new User();
        user.setUsername("testuser2");
        user.setPassword("testpass");
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        user.setRoles(Set.of(role));

        when(userRepository.findByUsername("testuser2")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testuser2");

        assertNotNull(userDetails);
        assertEquals("testuser2", userDetails.getUsername());
        assertEquals("testpass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }
}

