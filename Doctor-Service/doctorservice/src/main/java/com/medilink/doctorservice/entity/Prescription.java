package com.medilink.doctorservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String prescriptionId;

    @Field("doctor_id")
    private String doctorId;
    
    @Field("patient_id")
    private String patientId;

    @Field("medicines")
    private String medicines;

    @Field("notes")
    private String notes;

    @Field("created_at")
    private LocalDateTime createdAt;
}