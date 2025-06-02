package com.uds.project.service_authentification_compte.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uds.project.service_authentification_compte.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
