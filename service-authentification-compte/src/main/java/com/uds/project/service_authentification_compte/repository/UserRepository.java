package com.uds.project.service_authentification_compte.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.uds.project.service_authentification_compte.entity.User;

public interface UserRepository extends JpaRepository<User , Long> {
    User findByUsername(String username);
}

