package com.medilink.doctorservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionDTO {
    private String prescriptionId;
    private String doctorId;
    private String patientId;
    private String medicines;
    private String notes;
    private LocalDateTime createdAt;
}