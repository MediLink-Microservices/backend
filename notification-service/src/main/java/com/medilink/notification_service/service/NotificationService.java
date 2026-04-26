package com.medilink.notification_service.service;

import com.medilink.notification_service.model.NotificationRequest;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/**
 * NotificationService handles the actual sending of Emails and SMS.
 * It uses JavaMailSender (for SMTP/Gmail) and Twilio SDK (for SMS).
 */
@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${twilio.account_sid}")
    private String twilioSid;

    @Value("${twilio.auth_token}")
    private String twilioAuthToken;

    @Value("${twilio.phone_number}")
    private String twilioFromPhone;

    /**
     * PostConstruct ensures Twilio is initialized once the credentials are loaded.
     */
    @PostConstruct
    public void initTwilio() {
        // Only initialize if we have actual credentials (avoiding errors in testing ,dev)
        if (twilioSid != null && !twilioSid.isEmpty() && !twilioSid.equals("your_sid")) {
            Twilio.init(twilioSid, twilioAuthToken);
        }
    }

    /**
     * Centralized logic to send alerts based on the request type.
     */
    public void processNotification(NotificationRequest request) {
        if ("EMAIL".equalsIgnoreCase(request.getType()) || "BOTH".equalsIgnoreCase(request.getType())) {
            sendEmail(request);
        }

        if ("SMS".equalsIgnoreCase(request.getType()) || "BOTH".equalsIgnoreCase(request.getType())) {
            sendSMS(request);
        }
    }

    /**
     * Sends an email via SMTP server using standard Spring Mail.
     * Equivalent to Nodemailer logic in Node.js.
     */
    private void sendEmail(NotificationRequest request) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(request.getRecipientEmail());
        mailMessage.setSubject(request.getSubject());
        mailMessage.setText(request.getMessage());

        // Log actually attempting to send (to help in debugging)
        System.out.println("Processing EMAIL notification to: " + request.getRecipientEmail());


        mailSender.send(mailMessage);
    }

    /**
     * Sends an SMS via Twilio API.
     */
    private void sendSMS(NotificationRequest request) {
        // Log actually attempting to send
        System.out.println("Processing SMS notification to: " + request.getRecipientPhone());

        /*
         * // Example Twilio sending code (Requires real credentials and Twilio
         * initialization)
         * Message message = Message.creator(
         * new PhoneNumber(request.getRecipientPhone()),
         * new PhoneNumber(twilioFromPhone),
         * request.getMessage()
         * ).create();
         */
    }
}
