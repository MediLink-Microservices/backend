package com.medilink.appointmentservice.client;

import com.medilink.appointmentservice.client.dto.DoctorDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DoctorServiceClient {

    private final RestClient restClient;
    private final String doctorServiceBaseUrl;

    public DoctorServiceClient(
            RestClient restClient,
            @Value("${services.doctor.url}") String doctorServiceBaseUrl) {
        this.restClient = restClient;
        this.doctorServiceBaseUrl = doctorServiceBaseUrl;
    }

    public DoctorDetails getDoctorById(String doctorId) {
        return restClient.get()
                .uri(doctorServiceBaseUrl + "/api/doctors/" + doctorId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (httpRequest, httpResponse) -> {
                    throw new IllegalArgumentException("Doctor not found or unavailable.");
                })
                .body(DoctorDetails.class);
    }
}
