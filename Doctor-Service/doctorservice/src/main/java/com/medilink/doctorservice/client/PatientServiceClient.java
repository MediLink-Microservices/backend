package com.medilink.doctorservice.client;

import com.medilink.doctorservice.dto.PatientDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class PatientServiceClient {

    private final RestTemplate restTemplate;
    private final String patientServiceUrl;

    public PatientServiceClient(RestTemplate restTemplate, 
                               @Value("${services.patient.url:http://localhost:8082}") String patientServiceUrl) {
        this.restTemplate = restTemplate;
        this.patientServiceUrl = patientServiceUrl;
    }

    public PatientDTO getPatientById(String patientId) {
        try {
            String url = patientServiceUrl + "/api/patient/" + patientId;
            return restTemplate.getForObject(url, PatientDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Patient not found with ID: " + patientId);
        } catch (Exception e) {
            throw new RuntimeException("Error calling patient service: " + e.getMessage());
        }
    }

    public PatientDTO getPatientByEmail(String email) {
        try {
            String url = patientServiceUrl + "/api/patients/email/" + email;
            return restTemplate.getForObject(url, PatientDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Patient not found with email: " + email);
        } catch (Exception e) {
            throw new RuntimeException("Error calling patient service: " + e.getMessage());
        }
    }
}
