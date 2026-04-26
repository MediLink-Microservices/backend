package com.medilink.paymentservice.client;

import com.medilink.paymentservice.model.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AppointmentClient {

    private final RestClient restClient;
    private final String appointmentServiceBaseUrl;

    public AppointmentClient(
            RestClient restClient,
            @Value("${services.appointment.url}") String appointmentServiceBaseUrl) {
        this.restClient = restClient;
        this.appointmentServiceBaseUrl = appointmentServiceBaseUrl;
    }

    public void markAppointmentAsConfirmed(String appointmentId) {
        updateAppointmentStatus(appointmentId, "CONFIRMED");
    }

    public void markAppointmentAsPendingPayment(String appointmentId) {
        updateAppointmentStatus(appointmentId, "PENDING_PAYMENT");
    }

    private void updateAppointmentStatus(String appointmentId, String status) {
        restClient.put()
                .uri(appointmentServiceBaseUrl + "/appointments/{id}/status?status={status}", appointmentId, status)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new IllegalStateException("Failed to update appointment status. HTTP " + response.getStatusCode());
                })
                .toBodilessEntity();
    }
}
