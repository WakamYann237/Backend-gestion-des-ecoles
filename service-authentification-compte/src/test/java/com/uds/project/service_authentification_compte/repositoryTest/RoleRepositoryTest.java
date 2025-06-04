package com.uds.project.service_authentification_compte.repositoryTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.uds.project.service_authentification_compte.entity.Role;
import com.uds.project.service_authentification_compte.repository.RoleRepository;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByName() {
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);

        Optional<Role> foundRole = roleRepository.findByName("ADMIN");

        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo("ADMIN");
    }
}
