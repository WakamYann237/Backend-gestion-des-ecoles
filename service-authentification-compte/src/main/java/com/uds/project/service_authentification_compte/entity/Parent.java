package com.uds.project.service_authentification_compte.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parents")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Parent extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String adresse;
}
