package com.medilink.telemedicine_service.controller;

import com.medilink.telemedicine_service.service.TelemedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

/**
 * TelemedicineController provides endpoints for managing video sessions via Jitsi Meet.
 */
@RestController
@RequestMapping("/api/telemedicine")
@CrossOrigin(origins = "*") // Allow frontend to call directly (can be restricted by Gateway)
public class TelemedicineController {

    @Autowired
    private TelemedicineService telemedicineService;

    /**
     * Join/Generate a video call link for an appointment.
     * Use POST http://localhost:8088/api/telemedicine/join?appointmentId=XYZ
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
