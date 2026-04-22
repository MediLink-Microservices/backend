package com.medilink.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.medilink.paymentservice.client.AppointmentClient;
import com.medilink.paymentservice.client.NotificationClient;
import com.medilink.paymentservice.dto.NotificationRequest;
import com.medilink.paymentservice.dto.PaymentResponse;
import com.medilink.paymentservice.dto.ProcessPaymentRequest;
import com.medilink.paymentservice.model.Payment;
import com.medilink.paymentservice.model.PaymentStatus;
import com.medilink.paymentservice.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import java.time.ZoneOffset;
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
    private final String stripeSecretKey;
    private final String stripeWebhookSecret;
    private final String stripeSuccessUrl;
    private final String stripeCancelUrl;

    public PaymentService(
            PaymentRepository paymentRepository,
            AppointmentClient appointmentClient,
            NotificationClient notificationClient,
            @Value("${payment.currency}") String currency,
            @Value("${payment.gateway.provider}") String gatewayProvider,
            @Value("${payment.minimum-amount}") double minimumAmount,
            @Value("${payment.maximum-amount}") double maximumAmount,
            @Value("${payment.stripe.secret-key}") String stripeSecretKey,
            @Value("${payment.stripe.webhook-secret}") String stripeWebhookSecret,
            @Value("${payment.stripe.success-url}") String stripeSuccessUrl,
            @Value("${payment.stripe.cancel-url}") String stripeCancelUrl) {
        this.paymentRepository = paymentRepository;
        this.appointmentClient = appointmentClient;
        this.notificationClient = notificationClient;
        this.currency = currency;
        this.gatewayProvider = gatewayProvider;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.stripeSecretKey = stripeSecretKey;
        this.stripeWebhookSecret = stripeWebhookSecret;
        this.stripeSuccessUrl = stripeSuccessUrl;
        this.stripeCancelUrl = stripeCancelUrl;
    }

    @PostConstruct
    void initializeStripe() {
        if (stripeSecretKey != null && !stripeSecretKey.isBlank()) {
            Stripe.apiKey = stripeSecretKey;
        }
    }

    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        validateAmount(request.getAmount());
        validateStripeConfiguration();

        Payment payment = paymentRepository.findByAppointmentId(request.getAppointmentId()).orElseGet(Payment::new);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return toResponse(payment);
        }

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment = syncPendingPaymentWithStripe(payment);

            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return toResponse(payment);
            }

            if (payment.getStatus() == PaymentStatus.PENDING
                    && payment.getCheckoutUrl() != null
                    && !payment.getCheckoutUrl().isBlank()) {
                // Reuse active checkout session instead of failing the user flow.
                return toResponse(payment);
            }
        }

        payment.setAppointmentId(request.getAppointmentId());
        payment.setPatientId(request.getPatientId());
        payment.setAmount(request.getAmount());
        payment.setCurrency(currency);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setGatewayProvider(gatewayProvider);
        payment.setRecipientEmail(request.getRecipientEmail());
        payment.setRecipientPhone(request.getRecipientPhone());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionReference(null);
        payment.setCheckoutSessionId(null);
        payment.setCheckoutUrl(null);
        payment.setFailureReason(null);
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(LocalDateTime.now());
        }
        payment.setUpdatedAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        try {
            Session checkoutSession = createStripeCheckoutSession(savedPayment, request);
            savedPayment.setCheckoutSessionId(checkoutSession.getId());
            savedPayment.setCheckoutUrl(checkoutSession.getUrl());
            savedPayment.setUpdatedAt(LocalDateTime.now());
            savedPayment = paymentRepository.save(savedPayment);
        } catch (StripeException exception) {
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setFailureReason(exception.getMessage());
            savedPayment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(savedPayment);
            throw new IllegalStateException("Failed to create Stripe checkout session: " + exception.getMessage());
        }

        appointmentClient.markAppointmentAsPendingPayment(savedPayment.getAppointmentId());
        return toResponse(savedPayment);
    }

    private Payment syncPendingPaymentWithStripe(Payment payment) {
        if (payment.getCheckoutSessionId() == null || payment.getCheckoutSessionId().isBlank()) {
            return payment;
        }

        try {
            Session session = Session.retrieve(payment.getCheckoutSessionId());
            if ("paid".equalsIgnoreCase(session.getPaymentStatus())) {
                markPaymentSuccess(session);
                return paymentRepository.findById(payment.getId()).orElse(payment);
            }
            if ("expired".equalsIgnoreCase(session.getStatus())) {
                markPaymentFailed(session, "Stripe checkout session expired.");
                return paymentRepository.findById(payment.getId()).orElse(payment);
            }
        } catch (StripeException ignored) {
            // If Stripe is temporarily unavailable, keep local PENDING state and let user retry.
        }

        return payment;
    }

    public void handleStripeWebhook(String payload, String stripeSignature) {
        validateStripeWebhookConfiguration();

        final Event event;
        try {
            event = Webhook.constructEvent(payload, stripeSignature, stripeWebhookSecret);
        } catch (SignatureVerificationException exception) {
            throw new IllegalArgumentException("Invalid Stripe webhook signature.");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new IllegalStateException("Unable to deserialize Stripe checkout session."));
            markPaymentSuccess(session);
            return;
        }

        if ("checkout.session.expired".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new IllegalStateException("Unable to deserialize Stripe checkout session."));
            markPaymentFailed(session, "Stripe checkout session expired.");
        }
    }

    public PaymentResponse getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
    }

    public PaymentResponse getPaymentByAppointmentId(String appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .map(payment -> {
                    if (payment.getStatus() == PaymentStatus.PENDING) {
                        payment = syncPendingPaymentWithStripe(payment);
                    }
                    return toResponse(payment);
                })
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

    private void validateStripeConfiguration() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new IllegalStateException("Stripe secret key is not configured.");
        }
    }

    private void validateStripeWebhookConfiguration() {
        validateStripeConfiguration();
        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            throw new IllegalStateException("Stripe webhook secret is not configured.");
        }
    }

    private Session createStripeCheckoutSession(Payment payment, ProcessPaymentRequest request) throws StripeException {
        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(buildReturnUrl(stripeSuccessUrl, payment.getAppointmentId()))
                .setCancelUrl(buildReturnUrl(stripeCancelUrl, payment.getAppointmentId()))
                .putMetadata("paymentId", payment.getId())
                .putMetadata("appointmentId", payment.getAppointmentId())
                .putMetadata("patientId", payment.getPatientId())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency.toLowerCase())
                                                .setUnitAmount(Math.round(payment.getAmount() * 100))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Medilink Consultation Fee")
                                                                .setDescription("Appointment ID: " + payment.getAppointmentId())
                                                                .build())
                                                .build())
                                .build());

        if (request.getRecipientEmail() != null && !request.getRecipientEmail().isBlank()) {
            builder.setCustomerEmail(request.getRecipientEmail().trim());
        }

        return Session.create(builder.build());
    }

    private String buildReturnUrl(String template, String appointmentId) {
        return template.replace("{APPOINTMENT_ID}", appointmentId);
    }

    private void markPaymentSuccess(Session session) {
        Payment payment = resolvePaymentForStripeSession(session);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setFailureReason(null);
        payment.setTransactionReference(
                session.getPaymentIntent() != null ? session.getPaymentIntent() : generateTransactionReference());
        payment.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        paymentRepository.save(payment);
        appointmentClient.markAppointmentAsConfirmed(payment.getAppointmentId());
        triggerSuccessNotification(payment);
    }

    private void markPaymentFailed(Session session, String reason) {
        Payment payment = resolvePaymentForStripeSession(session);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        paymentRepository.save(payment);
        appointmentClient.markAppointmentAsPendingPayment(payment.getAppointmentId());
    }

    private Payment resolvePaymentForStripeSession(Session session) {
        if (session.getId() != null) {
            return paymentRepository.findByCheckoutSessionId(session.getId())
                    .orElseGet(() -> resolvePaymentByMetadata(session));
        }
        return resolvePaymentByMetadata(session);
    }

    private Payment resolvePaymentByMetadata(Session session) {
        String paymentId = session.getMetadata() != null ? session.getMetadata().get("paymentId") : null;
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalStateException("Stripe session is missing Medilink payment metadata.");
        }

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment not found for Stripe session."));
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void triggerSuccessNotification(Payment payment) {
        if ((payment.getRecipientEmail() == null || payment.getRecipientEmail().isBlank())
                && (payment.getRecipientPhone() == null || payment.getRecipientPhone().isBlank())) {
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
                payment.getRecipientEmail(),
                payment.getRecipientPhone(),
                "Appointment Payment Confirmed",
                message,
                resolveNotificationType(payment),
                "HIGH");

        try {
            notificationClient.sendPaymentSuccessNotification(notificationRequest);
        } catch (Exception exception) {
            System.err.println("Notification service call failed: " + exception.getMessage());
        }
    }

    private String resolveNotificationType(Payment payment) {
        boolean hasEmail = payment.getRecipientEmail() != null && !payment.getRecipientEmail().isBlank();
        boolean hasPhone = payment.getRecipientPhone() != null && !payment.getRecipientPhone().isBlank();

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
                .gatewayProvider(payment.getGatewayProvider())
                .checkoutSessionId(payment.getCheckoutSessionId())
                .checkoutUrl(payment.getCheckoutUrl())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
