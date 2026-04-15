package com.medilink.paymentservice.service;

import com.medilink.paymentservice.client.AppointmentClient;
import com.medilink.paymentservice.client.NotificationClient;
import com.medilink.paymentservice.dto.NotificationRequest;
import com.medilink.paymentservice.dto.PaymentResponse;
import com.medilink.paymentservice.dto.ProcessPaymentRequest;
import com.medilink.paymentservice.model.Payment;
import com.medilink.paymentservice.model.PaymentStatus;
import com.medilink.paymentservice.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentClient appointmentClient;
    private final NotificationClient notificationClient;
    private final String currency;
    private final String gatewayProvider;
    private final double minimumAmount;
    private final double maximumAmount;

    public PaymentService(
            PaymentRepository paymentRepository,
            AppointmentClient appointmentClient,
            NotificationClient notificationClient,
            @Value("${payment.currency}") String currency,
            @Value("${payment.gateway.provider}") String gatewayProvider,
            @Value("${payment.minimum-amount}") double minimumAmount,
            @Value("${payment.maximum-amount}") double maximumAmount) {
        this.paymentRepository = paymentRepository;
        this.appointmentClient = appointmentClient;
        this.notificationClient = notificationClient;
        this.currency = currency;
        this.gatewayProvider = gatewayProvider;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }

    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        validateAmount(request.getAmount());

        paymentRepository.findByAppointmentId(request.getAppointmentId()).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.SUCCESS) {
                throw new IllegalStateException("A successful payment already exists for this appointment.");
            }
        });

        Payment payment = new Payment();
        payment.setAppointmentId(request.getAppointmentId());
        payment.setPatientId(request.getPatientId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(currency);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setGatewayProvider(gatewayProvider);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        if (request.isSimulateSuccess()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionReference(generateTransactionReference());
            Payment savedPayment = paymentRepository.save(payment);
            appointmentClient.markAppointmentAsConfirmed(savedPayment.getAppointmentId());
            triggerSuccessNotification(savedPayment, request);
            return toResponse(savedPayment);
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason("Simulated payment failure");
        Payment savedPayment = paymentRepository.save(payment);
        appointmentClient.markAppointmentAsPendingPayment(savedPayment.getAppointmentId());
        return toResponse(savedPayment);
    }

    public PaymentResponse getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
    }

    public PaymentResponse getPaymentByAppointmentId(String appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for appointment."));
    }

    public List<PaymentResponse> getPaymentsByPatientId(String patientId) {
        return paymentRepository.findByPatientId(patientId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateAmount(double amount) {
        if (amount < minimumAmount || amount > maximumAmount) {
            throw new IllegalArgumentException(
                    "Payment amount must be between " + minimumAmount + " and " + maximumAmount + ".");
        }
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void triggerSuccessNotification(Payment payment, ProcessPaymentRequest request) {
        if ((request.getRecipientEmail() == null || request.getRecipientEmail().isBlank())
                && (request.getRecipientPhone() == null || request.getRecipientPhone().isBlank())) {
            return;
        }

        String message = "Your appointment payment was successful. Appointment ID: "
                + payment.getAppointmentId()
                + ", Amount: "
                + payment.getAmount()
                + " "
                + payment.getCurrency()
                + ", Transaction: "
                + payment.getTransactionReference();

        NotificationRequest notificationRequest = new NotificationRequest(
                request.getRecipientEmail(),
                request.getRecipientPhone(),
                "Appointment Payment Confirmed",
                message,
                resolveNotificationType(request),
                "HIGH");

        try {
            notificationClient.sendPaymentSuccessNotification(notificationRequest);
        } catch (Exception exception) {
            System.err.println("Notification service call failed: " + exception.getMessage());
        }
    }

    private String resolveNotificationType(ProcessPaymentRequest request) {
        boolean hasEmail = request.getRecipientEmail() != null && !request.getRecipientEmail().isBlank();
        boolean hasPhone = request.getRecipientPhone() != null && !request.getRecipientPhone().isBlank();

        if (hasEmail && hasPhone) {
            return "BOTH";
        }
        if (hasEmail) {
            return "EMAIL";
        }
        return "SMS";
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .appointmentId(payment.getAppointmentId())
                .patientId(payment.getPatientId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
