package com.healthcare.doctor_service.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PrescriptionRequestDTO {
    @NotNull(message = "Doctor ID is required")
    @Positive(message = "Doctor ID must be positive")
    private Long doctorId;

    @NotNull(message = "Patient ID is required")
    @Positive(message = "Patient ID must be positive")
    private Long patientId;

    @NotBlank(message = "Medicines are required")
    @Size(max = 1000, message = "Medicines field must not exceed 1000 characters")
    private String medicines;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
