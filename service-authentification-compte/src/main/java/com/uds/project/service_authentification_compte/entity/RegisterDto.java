package com.uds.project.service_authentification_compte.entity;

import java.util.Set;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String username;
    private String password;
    private Set<String> roleNames;

    public RegisterDto(String username, Set<String> roleNames) {
    this.username = username;
    this.roleNames = roleNames;
}

}
