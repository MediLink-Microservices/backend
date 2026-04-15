package com.medilink.paymentservice.client;

import com.medilink.paymentservice.dto.NotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationClient {

    private final RestClient restClient;
    private final String notificationServiceBaseUrl;

    public NotificationClient(
            RestClient restClient,
            @Value("${services.notification.url}") String notificationServiceBaseUrl) {
        this.restClient = restClient;
        this.notificationServiceBaseUrl = notificationServiceBaseUrl;
    }

    public void sendPaymentSuccessNotification(NotificationRequest request) {
        restClient.post()
                .uri(notificationServiceBaseUrl + "/api/notify")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (httpRequest, httpResponse) -> {
                    throw new IllegalStateException(
                            "Failed to send notification. HTTP " + httpResponse.getStatusCode());
                })
                .toBodilessEntity();
    }
}
