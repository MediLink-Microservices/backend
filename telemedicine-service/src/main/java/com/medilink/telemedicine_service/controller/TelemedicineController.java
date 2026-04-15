package com.medilink.telemedicine_service.controller;

import com.medilink.telemedicine_service.service.TelemedicineService;
import com.medilink.telemedicine_service.model.Telemedicine;
import com.medilink.telemedicine_service.dto.CreateTelemedicineRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.validation.Valid;

/**
 * TelemedicineController provides endpoints for managing video sessions via Jitsi Meet.
 */
@RestController
@RequestMapping("/api/telemedicine")
@CrossOrigin(origins = "*")
public class TelemedicineController {

    @Autowired
    private TelemedicineService telemedicineService;

    /**
     * Creates a new telemedicine session.
     * POST http://localhost:8088/api/telemedicine/create
     */
    @PostMapping("/create")
    public ResponseEntity<Telemedicine> createTelemedicineSession(@Valid @RequestBody CreateTelemedicineRequest request) {
        try {
            Telemedicine telemedicine = telemedicineService.createTelemedicineSession(request);
            return ResponseEntity.status(201).body(telemedicine);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    /**
     * Gets all telemedicine sessions for a doctor.
     * GET http://localhost:8088/api/telemedicine/doctor/{doctorId}
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Telemedicine>> getDoctorSessions(@PathVariable String doctorId) {
        List<Telemedicine> sessions = telemedicineService.getDoctorTelemedicineSessions(doctorId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Gets all telemedicine sessions for a patient.
     * GET http://localhost:8088/api/telemedicine/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Telemedicine>> getPatientSessions(@PathVariable String patientId) {
        List<Telemedicine> sessions = telemedicineService.getPatientTelemedicineSessions(patientId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Updates telemedicine session status.
     * PUT http://localhost:8088/api/telemedicine/{sessionId}/status
     */
    @PutMapping("/{sessionId}/status")
    public ResponseEntity<Telemedicine> updateSessionStatus(
            @PathVariable String sessionId,
            @RequestParam String status) {
        Telemedicine updatedSession = telemedicineService.updateSessionStatus(sessionId, status);
        if (updatedSession != null) {
            return ResponseEntity.ok(updatedSession);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    /**
     * Join/Generate a video call link for an appointment.
     * POST http://localhost:8088/api/telemedicine/join?appointmentId=XYZ
     */
    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> joinVideoCall(@RequestParam String appointmentId) {
        try {
            // Generate the dynamic Jitsi URL (enforces Confirmation logic internally)
            String jitsiUrl = telemedicineService.generateMeetingUrl(appointmentId);
            
            // Build the response object with the URL and a success message
            Map<String, String> response = new HashMap<>();
            response.put("appointmentId", appointmentId);
            response.put("jitsiUrl", jitsiUrl);
            response.put("status", "Confirmed");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return error response (e.g. appointment not confirmed)
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("status", "Access Denied");
            
            return ResponseEntity.status(403).body(errorResponse);
        }
    }
}
