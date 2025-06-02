package com.uds.project.service_authentification_compte.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proviseurs")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Proviseur extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String etablissement;
}
