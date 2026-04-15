package com.medilink.appointmentservice.client;

import com.medilink.appointmentservice.client.dto.PatientDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PatientServiceClient {

    private final RestClient restClient;
    private final String patientServiceBaseUrl;

    public PatientServiceClient(
            RestClient restClient,
            @Value("${services.patient.url}") String patientServiceBaseUrl) {
        this.restClient = restClient;
        this.patientServiceBaseUrl = patientServiceBaseUrl;
    }

    public PatientDetails getPatientById(String patientId) {
        return restClient.get()
                .uri(patientServiceBaseUrl + "/api/patient/" + patientId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (httpRequest, httpResponse) -> {
                    throw new IllegalArgumentException("Patient not found or unavailable.");
                })
                .body(PatientDetails.class);
    }
}
