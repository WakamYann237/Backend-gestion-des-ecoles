package com.uds.project.service_authentification_compte.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cuisiniers")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cuisinier extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
