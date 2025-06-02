package com.kidschool.serviceabsence.absence;


import org.springframework.stereotype.Component;

@Component
public class AbsenceMapper {
    public AbsenceResponse toResponse(Absence absence) {
        return AbsenceResponse.builder()
                .id(absence.getId())
                .studentId(absence.getStudentId())
                .reason(absence.getReason())
                .status(absence.getStatus().name())
                .date(absence.getDate())
                .updateDate(absence.getUpdateDate())
                .build();
    }

    public Absence toEntity(AbsenceRequest request) {
        return Absence.builder()
                .studentId(request.getStudentId())
                .reason(request.getReason())
                .status(AbsenceStatus.valueOf(request.getStatus().toUpperCase()))
                .build();
    }
}
