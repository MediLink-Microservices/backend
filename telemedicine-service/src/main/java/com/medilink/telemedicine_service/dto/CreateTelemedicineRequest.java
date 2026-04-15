package com.medilink.telemedicine_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTelemedicineRequest {
    
    @NotBlank
    private String doctorId;
    
    @NotBlank
    private String patientId;
    
    @NotBlank
    private String patientName;
    
    @NotBlank
    private String doctorName;
    
    @NotBlank
    private String doctorSpecialty;
    
    @NotBlank
    private String consultationType;
    
    @NotNull
    @Future
    private LocalDateTime appointmentDateTime;
    
    private String notes;
}
