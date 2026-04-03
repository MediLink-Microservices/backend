package com.medilink.doctorservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class PrescriptionRequestDTO {
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    @NotBlank(message = "Diagnosis is required")
    @Size(max = 500, message = "Diagnosis must not exceed 500 characters")
    private String diagnosis;

    @NotBlank(message = "Medicines are required")
    @Size(max = 1000, message = "Medicines field must not exceed 1000 characters")
    private String medicines;
    
    @NotBlank(message = "Dosage instructions are required")
    @Size(max = 500, message = "Dosage instructions must not exceed 500 characters")
    private String dosageInstructions;
    
    @NotBlank(message = "Duration is required")
    @Size(max = 100, message = "Duration must not exceed 100 characters")
    private String duration;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
