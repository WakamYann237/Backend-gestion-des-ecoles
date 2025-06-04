package com.uds.project.service_authentification_compte.repositoryTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.uds.project.service_authentification_compte.entity.User;
import com.uds.project.service_authentification_compte.repository.UserRepository;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("testuser3");
        userRepository.save(user);

        User foundUser = userRepository.findByUsername("testuser3");

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo("testuser3");
    }
}

