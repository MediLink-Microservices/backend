package com.medilink.doctorservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class PatientDTO {
    private String patientId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String province;
    private String nic;
    private LocalDateTime dateOfBirth;
    private String gender;
    private Boolean isActive;
}
