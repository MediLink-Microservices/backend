package com.medilink.notification_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * NotificationRequest represents the payload for triggering a notification.
 * It's used by other services via the /notify endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String recipientEmail; // Target email for Nodemailer logic
    private String recipientPhone; // Target phone for Twilio SMS logic
    private String subject;        // Email subject
    private String message;        // Notification body content
    private String type;           // e.g. "EMAIL", "SMS", "BOTH"
    private String priority;       // e.g. "HIGH", "NORMAL"
}
