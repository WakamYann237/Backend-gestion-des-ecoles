package com.kidschool.serviceabsence.absence;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kidschool.serviceabsence.student.StudentResponse;
import com.kidschool.serviceabsence.student.StudentServiceClient;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AbsenceServiceTest {

    @Mock
    private AbsenceRepository absenceRepository;

    @Mock
    private StudentServiceClient studentServiceClient;

    @Mock
    private AbsenceMapper absenceMapper;

    @InjectMocks
    private AbsenceServiceImpl absenceService;

    private AbsenceRequest absenceRequest;
    private Absence absence;
    private AbsenceResponse absenceResponse;
    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        absenceRequest = AbsenceRequest.builder()
                .studentId(1L)
                .reason("Illness")
                .status("JUSTIFIED")
                .build();

        absence = Absence.builder()
                .id(1L)
                .studentId(1L)
                .reason("Illness")
                .status(AbsenceStatus.JUSTIFIED)
                .date(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        absenceResponse = AbsenceResponse.builder()
                .id(1L)
                .studentId(1L)
                .reason("Illness")
                .status("JUSTIFIED")
                .date(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        studentResponse = StudentResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void createAbsence_Success() {
        when(studentServiceClient.getStudentById(1L)).thenReturn(studentResponse);
        when(absenceMapper.toEntity(any(AbsenceRequest.class))).thenReturn(absence);
        when(absenceRepository.save(any(Absence.class))).thenReturn(absence);
        when(absenceMapper.toResponse(any(Absence.class))).thenReturn(absenceResponse);

        AbsenceResponse result = absenceService.createAbsence(absenceRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("JUSTIFIED", result.getStatus());
        verify(absenceRepository).save(any(Absence.class));
    }

    @Test
    void createAbsence_StudentNotFound() {
        when(studentServiceClient.getStudentById(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> 
            absenceService.createAbsence(absenceRequest)
        );
    }

    @Test
    void getAbsenceById_Success() {
        when(absenceRepository.findById(1L)).thenReturn(Optional.of(absence));
        when(absenceMapper.toResponse(any(Absence.class))).thenReturn(absenceResponse);

        AbsenceResponse result = absenceService.getAbsenceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAbsenceById_NotFound() {
        when(absenceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            absenceService.getAbsenceById(1L)
        );
    }

    @Test
    void getAllAbsences_Success() {
        List<Absence> absences = Arrays.asList(absence);
        when(absenceRepository.findAll()).thenReturn(absences);
        when(studentServiceClient.getStudentById(anyLong())).thenReturn(studentResponse);
        when(absenceMapper.toResponse(any(Absence.class))).thenReturn(absenceResponse);

        List<AbsenceResponse> results = absenceService.getAllAbsences();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void getAbsencesByStudentId_Success() {
        List<Absence> absences = Arrays.asList(absence);
        when(absenceRepository.findAbsencesById(1L)).thenReturn(absences);
        when(absenceMapper.toResponse(any(Absence.class))).thenReturn(absenceResponse);

        List<AbsenceResponse> results = absenceService.getAbsencesByStudentId(1L);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }
} 