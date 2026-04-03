package com.medilink.appointmentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    @NotBlank
    private String patientId;

    @NotBlank
    private String doctorId;

    @NotBlank
    private String doctorName;

    @NotBlank
    private String doctorSpecialty;

    @NotBlank
    private String doctorHospital;

    @DecimalMin("0.0")
    private double consultationFee;

    @NotNull
    @Future
    private LocalDateTime appointmentDateTime;

    private String notes;
}
