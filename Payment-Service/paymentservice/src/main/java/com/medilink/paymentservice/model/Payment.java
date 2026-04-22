package com.medilink.paymentservice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String appointmentId;
    private String patientId;
    private double amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private String gatewayProvider;
    private String checkoutSessionId;
    private String checkoutUrl;
    private String recipientEmail;
    private String recipientPhone;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
