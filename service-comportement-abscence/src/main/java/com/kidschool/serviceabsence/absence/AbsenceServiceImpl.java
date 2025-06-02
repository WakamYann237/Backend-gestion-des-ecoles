package com.kidschool.serviceabsence.absence;

import com.kidschool.serviceabsence.student.StudentResponse;
import com.kidschool.serviceabsence.student.StudentServiceClient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbsenceServiceImpl implements AbsenceService {
    private final AbsenceMapper absenceMapper;
    private final StudentServiceClient studentServiceClient;
    private final AbsenceRepository absenceRepository;

    public AbsenceServiceImpl(AbsenceMapper absenceMapper, StudentServiceClient studentServiceClient, AbsenceRepository absenceRepository) {
        this.absenceMapper = absenceMapper;
        this.studentServiceClient = studentServiceClient;
        this.absenceRepository = absenceRepository;
    }

    @Override
    public AbsenceResponse createAbsence(AbsenceRequest absenceRequest) {
        Long studentId = absenceRequest.getStudentId();
        StudentResponse studentResponse =  studentServiceClient.getStudentById(studentId);
        if (studentResponse == null) {
            throw new IllegalArgumentException("Student not found with id: " + studentId);
        }
        Absence absence = absenceMapper.toEntity(absenceRequest);
        absence.setStudentId(studentId);
        Absence savedAbsence = absenceRepository.save(absence);
        return absenceMapper.toResponse(savedAbsence);
    }

    @Override
    public AbsenceResponse getAbsenceById(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Absence not found with id: " + id));
        return absenceMapper.toResponse(absence);
    }

    @Override
    public AbsenceResponse updateAbsence(Long id, AbsenceRequest absenceRequest) {
        Absence existingAbsence = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Absence not found with id: " + id));
        Absence updatedAbsence = absenceMapper.toEntity(absenceRequest);
        updatedAbsence.setId(existingAbsence.getId());
        Absence savedAbsence = absenceRepository.save(updatedAbsence);

        return absenceMapper.toResponse(savedAbsence);
    }

    @Override
    public void deleteAbsence(Long id) {
        Absence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Absence not found with id: " + id));
        absenceRepository.delete(absence);

    }

    @Override
    public List<AbsenceResponse> getAllAbsences() {
        List<Absence> absences = absenceRepository.findAll();
        return absences.stream().map(absence -> {
            AbsenceResponse absenceResponse = absenceMapper.toResponse(absence);
            StudentResponse studentResponse = studentServiceClient.getStudentById(absence.getStudentId());
            absenceResponse.setStudentId(studentResponse.getId());
            return absenceResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AbsenceResponse> getAbsencesByStudentId(Long studentId) {
        List<Absence> absences = absenceRepository.findAbsencesById(studentId);
        return absences.stream().map(absenceMapper::toResponse).collect(Collectors.toList());
    }
}
