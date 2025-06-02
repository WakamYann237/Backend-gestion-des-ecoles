package com.kidschool.serviceabsence.absence;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbsenceRequest {
    @NotNull(message = "Student ID cannot be null")
    private Long studentId;
    private String reason;
    @NotNull(message = "Status cannot be null")
    private String status;
}
