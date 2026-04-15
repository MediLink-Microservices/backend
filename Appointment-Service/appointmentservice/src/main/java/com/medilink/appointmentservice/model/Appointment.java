package com.medilink.appointmentservice.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    private String patientId;
    private String doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private String doctorHospital;
    private double consultationFee;
    private String consultationType; // e.g., "online", "offline"

    private LocalDateTime appointmentDateTime;
    private int durationMinutes;
    private Integer appointmentNumber;
    private AppointmentStatus status;
    private String reasonForCancellation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
}
