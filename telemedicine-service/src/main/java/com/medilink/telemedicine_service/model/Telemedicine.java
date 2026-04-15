package com.medilink.telemedicine_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "telemedicine")
public class Telemedicine {

    @Id
    private String id;

    private String doctorId;
    private String patientId;
    private String patientName;
    private String doctorName;
    private String doctorSpecialty;
    private String consultationType;

    private LocalDateTime appointmentDateTime;
    private int durationMinutes;
    private String jitsiUrl;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
}
