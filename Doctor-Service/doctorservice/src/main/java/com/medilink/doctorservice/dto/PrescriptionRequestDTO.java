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

    @NotBlank(message = "Medicines are required")
    @Size(max = 1000, message = "Medicines field must not exceed 1000 characters")
    private String medicines;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
