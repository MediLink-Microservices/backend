package com.medilink.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String recipientEmail;
    private String recipientPhone;
    private String subject;
    private String message;
    private String type;
    private String priority;
}
