package com.kidschool.serviceabsence.absence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kidschool.serviceabsence.student.StudentResponse;
import com.kidschool.serviceabsence.student.StudentServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AbsenceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentServiceClient studentServiceClient;

    @Test
    void createAbsence_Success() throws Exception {
        // Given
        AbsenceRequest request = AbsenceRequest.builder()
                .studentId(1L)
                .reason("Illness")
                .status("JUSTIFIED")
                .build();

        StudentResponse studentResponse = StudentResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        when(studentServiceClient.getStudentById(anyLong())).thenReturn(studentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/absences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createAbsence_StudentNotFound() throws Exception {
        // Given
        AbsenceRequest request = AbsenceRequest.builder()
                .studentId(1L)
                .reason("Illness")
                .status("JUSTIFIED")
                .build();

        when(studentServiceClient.getStudentById(anyLong())).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/v1/absences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAbsenceById_Success() throws Exception {
        // Given
        Long absenceId = 1L;

        // When & Then
        mockMvc.perform(get("/api/v1/absences/{id}", absenceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getAbsenceById_NotFound() throws Exception {
        // Given
        Long absenceId = 999L;

        // When & Then
        mockMvc.perform(get("/api/v1/absences/{id}", absenceId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllAbsences_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/absences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getAbsencesByStudentId_Success() throws Exception {
        // Given
        Long studentId = 1L;

        // When & Then
        mockMvc.perform(get("/api/v1/absences/{studentId}/absences", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
} 