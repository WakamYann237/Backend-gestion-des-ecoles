package com.uds.project.service_authentification_compte.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uds.project.service_authentification_compte.configuration.JwtUtils;
import com.uds.project.service_authentification_compte.entity.RegisterDto;
import com.uds.project.service_authentification_compte.entity.Role;
import com.uds.project.service_authentification_compte.entity.User;
import com.uds.project.service_authentification_compte.exception.UserNotFoundException;
import com.uds.project.service_authentification_compte.repository.RoleRepository;
import com.uds.project.service_authentification_compte.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

@PostMapping("/user/register")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
    if (userRepository.findByUsername(registerDto.getUsername()) != null) {
        return ResponseEntity.badRequest().body("Username already exists");
    }

    User user = new User();
    user.setUsername(registerDto.getUsername());
    user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

    Set<Role> roles = new HashSet<>();
    for (String roleName : registerDto.getRoleNames()) {
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        roles.add(role);
    }
    user.setRoles(roles);

    // 🔹 Sauvegarde d'abord
    User savedUser = userRepository.save(user);

    // 🔹 Création du DTO de réponse
    Set<String> roleNames = savedUser.getRoles()
        .stream()
        .map(Role::getName)
        .collect(Collectors.toSet());

    RegisterDto userDto = new RegisterDto(savedUser.getUsername(), roleNames);

    return ResponseEntity.ok(userDto);
}

@PostMapping("/user/login")
public ResponseEntity<?> login(@RequestBody User user) {
    if (user.getUsername() == null || user.getPassword() == null) {
    return ResponseEntity.badRequest().body("Username and password are required.");
}

        try {

            // Authentifie l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            // Si authentification réussie, génère le token
            if (authentication.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("token", jwtUtils.generateToken(user.getUsername()));
                authData.put("type", "Bearer");
                return ResponseEntity.ok(authData);
            }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or Password");
        }
    }
    @GetMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<RegisterDto>> getAllPersonnes() {
    List<User> users = userRepository.findAll();

    List<RegisterDto> userDtos = users.stream()
        .map(user -> {
            Set<String> roleNames = user.getRoles()
                                        .stream()
                                        .map(Role::getName)
                                        .collect(Collectors.toSet());
            return new RegisterDto(user.getUsername(), null, roleNames);
        })
        .collect(Collectors.toList());

    return ResponseEntity.ok(userDtos);
}

    @GetMapping("/user/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<RegisterDto> getElementById(@PathVariable Long id) {
    User user = userRepository.findById(id)
                              .orElseThrow(() -> new UserNotFoundException("Personne not found."));
    Set<String> roleNames = user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet());
    RegisterDto dto = new RegisterDto(user.getUsername(), roleNames);
    return ResponseEntity.ok(dto);
}
@PutMapping("/user/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<RegisterDto> updateUser(@PathVariable Long id, @RequestBody RegisterDto userDetail) {
    User existUser = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found."));

    existUser.setUsername(userDetail.getUsername());

    if (userDetail.getPassword() != null && !userDetail.getPassword().isEmpty()) {
        existUser.setPassword(passwordEncoder.encode(userDetail.getPassword()));
    }

    // Mise à jour des rôles AVANT la sauvegarde
    if (userDetail.getRoleNames() != null && !userDetail.getRoleNames().isEmpty()) {
        Set<Role> roles = userDetail.getRoleNames().stream()
            .map(roleName -> roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
            .collect(Collectors.toSet());
        existUser.setRoles(roles);
    }

    // Sauvegarde de l'utilisateur avec tous les changements (username, password, roles)
    User updatedUser = userRepository.save(existUser);
    Set<String> roleNames = updatedUser.getRoles()
                                       .stream()
                                       .map(Role::getName)
                                       .collect(Collectors.toSet());

    RegisterDto dto = new RegisterDto(updatedUser.getUsername(), roleNames);

    return ResponseEntity.ok(dto);
}
@Transactional
@DeleteMapping("/user/delete/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<String> deleteUser(@PathVariable Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found."));

    String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    if (user.getUsername().equals(currentUsername)) {
        throw new IllegalArgumentException("You cannot delete your own account.");
    }

    // Vider les rôles avant suppression (à cause de la contrainte FK)
    user.getRoles().clear();
    userRepository.save(user); // pour appliquer les modifications dans la table intermédiaire

    userRepository.delete(user);

    return ResponseEntity.ok("Utilisateur supprimé avec succès.");
}


}
