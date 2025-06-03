package com.uds.project.service_authentification_compte.entityTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.uds.project.service_authentification_compte.entity.Role;
import com.uds.project.service_authentification_compte.entity.User;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public class UserRoleIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testPersistUserWithRole() {
        Role role = Role.builder()
            .name("ROLE_ADMIN")
            .description("Administrator role")
            .users(new HashSet<>())
            .build();

        User user = User.builder()
            .username("testuser")
            .password("securepass")
            .roles(new HashSet<>())
            .build();

        // Lier les deux entités
        user.getRoles().add(role);
        role.getUsers().add(user);

        // Sauvegarder
        entityManager.persist(role);
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        // Vérification
        User foundUser = entityManager.find(User.class, user.getId());
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals(1, foundUser.getRoles().size());

        Role foundRole = entityManager.find(Role.class, role.getId());
        assertNotNull(foundRole);
        assertEquals("ADMIN", foundRole.getName());
        assertEquals(1, foundRole.getUsers().size());
    }

    @Test
    public void testUniqueUsernameConstraint() {
        User user1 = User.builder()
            .username("duplicateuser")
            .password("pass1")
            .roles(new HashSet<>())
            .build();

        User user2 = User.builder()
            .username("duplicateuser")
            .password("pass2")
            .roles(new HashSet<>())
            .build();

        entityManager.persist(user1);
        entityManager.flush();

        assertThrows(PersistenceException.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }
}
