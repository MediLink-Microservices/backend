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
    
    @Field("diagnosis")
    private String diagnosis;

    @Field("medicines")
    private String medicines;
    
    @Field("dosage_instructions")
    private String dosageInstructions;
    
    @Field("duration")
    private String duration;

    @Field("notes")
    private String notes;
    
    @Field("prescribed_date")
    private LocalDateTime prescribedDate;

    @Field("created_at")
    private LocalDateTime createdAt;
}