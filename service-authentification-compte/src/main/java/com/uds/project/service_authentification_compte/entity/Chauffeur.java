package com.uds.project.service_authentification_compte.entity;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "chauffeurs")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Chauffeur extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroPermis;
    private String zoneAffectation;
}
