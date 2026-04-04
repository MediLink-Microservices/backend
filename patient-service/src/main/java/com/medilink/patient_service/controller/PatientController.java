package com.medilink.patient_service.controller;

import com.medilink.patient_service.model.PatientProfile;
import com.medilink.patient_service.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

/**
 * PatientController provides endpoints for managing patient profiles and uploading reports.
 */
@RestController
@RequestMapping("/api/patient")
@CrossOrigin(origins = "*") // CORS for direct frontend interaction (can be limited by Gateway)
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Get a patient by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientProfile> getPatient(@PathVariable String id) {
        return patientService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create or update a patient profile.
     */
    @PostMapping("/profile")
    public ResponseEntity<PatientProfile> createOrUpdateProfile(@RequestBody PatientProfile profile) {
        return ResponseEntity.ok(patientService.saveProfile(profile));
    }

    /**
     * Delete a patient profile.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        patientService.deleteProfile(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Upload a medical report (PDF/Image) for a patient.
     * Use POST with form-data: uploadReport(patientId, title, file)
     */
    @PostMapping("/{patientId}/upload")
    public ResponseEntity<PatientProfile> uploadMedicalReport(
            @PathVariable String patientId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {
        try {
            // Validate file (e.g. size or extension)
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Upload report using Service (Cloudinary integration)
            PatientProfile updatedPatient = patientService.uploadReport(patientId, title, file);
            return ResponseEntity.ok(updatedPatient);
        } catch (IOException e) {
            // Handle upload error (e.g. Cloudinary communication failure)
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            // Handle patient not found or other runtime exceptions
            return ResponseEntity.notFound().build();
        }
    }
}
