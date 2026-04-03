package com.medilink.doctorservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class ScheduleDTO {
    private String scheduleId;

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotBlank(message = "Day is required")
    @Pattern(regexp = "^(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)$", 
             message = "Day must be a valid day of the week")
    private String day;

    @NotBlank(message = "Start time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", 
             message = "Start time must be in HH:MM format")
    private String startTime;

    @NotBlank(message = "End time is required")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", 
             message = "End time must be in HH:MM format")
    private String endTime;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Consultation type is required")
    @Pattern(regexp = "^(IN_PERSON|ONLINE|BOTH)$", 
             message = "Consultation type must be IN_PERSON, ONLINE, or BOTH")
    private String consultationType;
    
    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;

    @NotNull(message = "Patient limit is required")
    @Positive(message = "Patient limit must be positive")
    private Integer patientLimit;
}