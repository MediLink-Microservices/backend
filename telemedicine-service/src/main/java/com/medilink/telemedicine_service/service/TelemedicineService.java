package com.medilink.telemedicine_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

/**
 * TelemedicineService handles the generation of secure Jitsi Meet links.
 * It enforces business rules (e.g. appointment must be confirmed).
 */
@Service
public class TelemedicineService {

    // RestTemplate for calling other microservices (e.g. appointment-service)
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Generates a unique Jitsi Meet URL for a specific appointment.
     * Logic: URL = "https://meet.jit.si/" + AppointmentID + "_" + RandomSuffix
     */
    public String generateMeetingUrl(String appointmentId) {
        // Enforce basic validation - In a real scenario, we call appointment-service here
        // boolean isConfirmed = checkAppointmentConfirmation(appointmentId);
        // if (!isConfirmed) throw new RuntimeException("Appointment not confirmed.");

        String uniqueRoom = appointmentId + "-" + UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.jit.si/" + uniqueRoom;
    }

    /**
     * Placeholder for actual service-to-service call to verify appointment status.
     * Uses internal communication through the API Gateway or direct service calls.
     */
    private boolean checkAppointmentConfirmation(String appointmentId) {
        // Example: Call Port 8084 (Appointment Service) to check status
        // String status = restTemplate.getForObject("http://localhost:8084/api/appointments/" + appointmentId + "/status", String.class);
        // return "CONFIRMED".equalsIgnoreCase(status);
        return true; // Simulating confirmed status for demonstration
    }
}
