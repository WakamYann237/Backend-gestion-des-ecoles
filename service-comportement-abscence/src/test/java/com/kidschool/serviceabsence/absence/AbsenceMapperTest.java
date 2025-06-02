package com.kidschool.serviceabsence.absence;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbsenceMapperTest {

    private AbsenceMapper absenceMapper;
    private AbsenceRequest absenceRequest;
    private Absence absence;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        absenceMapper = new AbsenceMapper();
        now = LocalDateTime.now();
        
        // Setup test data for request
        absenceRequest = AbsenceRequest.builder()
                .studentId(1L)
                .reason("Test absence")
                .status("JUSTIFIED")
                .build();

        // Setup test data for entity
        absence = Absence.builder()
                .id(1L)
                .studentId(1L)
                .reason("Test absence")
                .status(AbsenceStatus.JUSTIFIED)
                .date(now)
                .updateDate(now)
                .build();
    }

    @Test
    void toResponse_ShouldMapAllFieldsCorrectly() {
        // When
        AbsenceResponse response = absenceMapper.toResponse(absence);

        // Then
        assertNotNull(response);
        assertEquals(absence.getId(), response.getId());
        assertEquals(absence.getStudentId(), response.getStudentId());
        assertEquals(absence.getReason(), response.getReason());
        assertEquals(absence.getStatus().name(), response.getStatus());
        assertEquals(absence.getDate(), response.getDate());
        assertEquals(absence.getUpdateDate(), response.getUpdateDate());
    }

    @Test
    void toResponse_ShouldHandleNullValues() {
        // Given
        Absence absenceWithNulls = Absence.builder()
                .id(1L)
                .studentId(1L)
                .status(AbsenceStatus.JUSTIFIED)
                .build();

        // When
        AbsenceResponse response = absenceMapper.toResponse(absenceWithNulls);

        // Then
        assertNotNull(response);
        assertEquals(absenceWithNulls.getId(), response.getId());
        assertEquals(absenceWithNulls.getStudentId(), response.getStudentId());
        assertNull(response.getReason());
        assertEquals(absenceWithNulls.getStatus().name(), response.getStatus());
        assertNull(response.getDate());
        assertNull(response.getUpdateDate());
    }

    @Test
    void toEntity_ShouldMapAllFieldsCorrectly() {
        // When
        Absence entity = absenceMapper.toEntity(absenceRequest);

        // Then
        assertNotNull(entity);
        assertEquals(absenceRequest.getStudentId(), entity.getStudentId());
        assertEquals(absenceRequest.getReason(), entity.getReason());
        assertEquals(AbsenceStatus.valueOf(absenceRequest.getStatus().toUpperCase()), entity.getStatus());
    }

    @Test
    void toEntity_ShouldHandleNullValues() {
        // Given
        AbsenceRequest requestWithNulls = AbsenceRequest.builder()
                .studentId(1L)
                .status("JUSTIFIED")
                .build();

        // When
        Absence entity = absenceMapper.toEntity(requestWithNulls);

        // Then
        assertNotNull(entity);
        assertEquals(requestWithNulls.getStudentId(), entity.getStudentId());
        assertNull(entity.getReason());
        assertEquals(AbsenceStatus.valueOf(requestWithNulls.getStatus().toUpperCase()), entity.getStatus());
    }

    @Test
    void toEntity_ShouldThrowExceptionForInvalidStatus() {
        // Given
        AbsenceRequest requestWithInvalidStatus = AbsenceRequest.builder()
                .studentId(1L)
                .status("INVALID_STATUS")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            absenceMapper.toEntity(requestWithInvalidStatus)
        );
    }
} 