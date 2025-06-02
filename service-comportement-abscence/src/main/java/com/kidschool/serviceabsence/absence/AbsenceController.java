package com.kidschool.serviceabsence.absence;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RequestMapping ("/api/v1/absences")
@RestController
public class AbsenceController {

    private final AbsenceService absenceService;

    public AbsenceController(AbsenceService absenceService) {
        this.absenceService = absenceService;
    }

    @GetMapping
    public ResponseEntity<List<AbsenceResponse>> getAllAbsences() {
        List<AbsenceResponse> absences = absenceService.getAllAbsences();
        return ResponseEntity.ok(absences);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AbsenceResponse> getAbsenceById(@PathVariable Long id) {
        AbsenceResponse absence = absenceService.getAbsenceById(id);
        return ResponseEntity.ok(absence);
    }

    @PostMapping
    public ResponseEntity<AbsenceResponse> createAbsence(@Valid  @RequestBody AbsenceRequest absenceRequest) {
        AbsenceResponse createdAbsence = absenceService.createAbsence(absenceRequest);
        return ResponseEntity.created(URI.create("/api/v1/absences/" + createdAbsence.getId())).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AbsenceResponse> updateAbsence(@Valid @PathVariable Long id, @RequestBody AbsenceRequest absenceRequest) {
        AbsenceResponse updatedAbsence = absenceService.updateAbsence(id, absenceRequest);
        return ResponseEntity.ok(updatedAbsence);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAbsence(@PathVariable Long id) {
        absenceService.deleteAbsence(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{studentId}/absences")
    public ResponseEntity<List<AbsenceResponse>> getAbsencesByStudentId(@PathVariable Long studentId) {
        List<AbsenceResponse> absences = absenceService.getAbsencesByStudentId(studentId);
        return ResponseEntity.ok(absences);
    }

}
