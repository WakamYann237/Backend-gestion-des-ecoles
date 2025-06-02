package com.uds.project.service_authentification_compte.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "professeurs")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Professeur extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialite;
}
