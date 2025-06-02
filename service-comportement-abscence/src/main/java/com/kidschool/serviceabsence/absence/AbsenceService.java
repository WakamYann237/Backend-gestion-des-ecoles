package com.kidschool.serviceabsence.absence;

import java.util.List;

public interface AbsenceService {
    AbsenceResponse createAbsence(AbsenceRequest absenceRequest);

    AbsenceResponse getAbsenceById(Long id);

    AbsenceResponse updateAbsence(Long id, AbsenceRequest absenceRequest);

    void deleteAbsence(Long id);

    List<AbsenceResponse> getAllAbsences();

    List<AbsenceResponse> getAbsencesByStudentId(Long studentId);

}
