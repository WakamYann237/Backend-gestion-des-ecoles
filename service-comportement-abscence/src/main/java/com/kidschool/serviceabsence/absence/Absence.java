package com.kidschool.serviceabsence.absence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "absences")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Absence {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "student_id",  nullable = false, updatable = false)
    private Long studentId;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "absence_date")
    @CreationTimestamp
    private LocalDateTime date;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Enumerated(EnumType.STRING)
    private AbsenceStatus status;

}
