package com.medilink.notification_service.controller;

import com.medilink.notification_service.model.NotificationRequest;
import com.medilink.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * NotificationController provides an internal endpoint for other services to trigger alerts.
 */
@RestController
@RequestMapping("/api/notify")
@CrossOrigin(origins = "*") // CORS for gateway and cross-origin frontend
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Trigger a new notification (Email, SMS or Both).
     * Endpoint: POST http://localhost:8087/api/notify
     */
    @PostMapping
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        try {
            // Process the notification using both Channel (Email/SMS) as requested in the payload
            notificationService.processNotification(request);
            
            // Return success response to the calling service
            return ResponseEntity.ok("Notification request processed and queued for sending.");
        } catch (Exception e) {
            // Handle notification processing errors (e.g. SMTP failure)
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
