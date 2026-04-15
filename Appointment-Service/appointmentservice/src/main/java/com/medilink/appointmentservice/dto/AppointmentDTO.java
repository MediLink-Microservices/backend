package com.medilink.appointmentservice.dto;

import com.medilink.appointmentservice.model.AppointmentStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private String id;
    private String patientId;
    private String doctorId;
    private String doctorName;
    private String doctorSpecialty;
    private double consultationFee;
    private String consultationType;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String notes;
    private Integer appointmentNumber;
}
