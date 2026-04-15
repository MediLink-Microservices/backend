package com.medilink.appointmentservice.client.dto;

import lombok.Data;

@Data
public class PatientDetails {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
