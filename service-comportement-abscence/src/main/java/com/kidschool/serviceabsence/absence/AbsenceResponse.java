package com.kidschool.serviceabsence.absence;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AbsenceResponse {
    private Long id;
    private Long studentId;
    private String reason;
    private String status;
    private LocalDateTime date;
    private LocalDateTime updateDate;

}
