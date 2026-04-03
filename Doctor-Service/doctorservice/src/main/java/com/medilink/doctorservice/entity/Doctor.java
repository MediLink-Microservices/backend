package com.medilink.doctorservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "doctors")
public class Doctor {

    @Id
    private String doctorId;

    @Field("name")
    private String name;
    
    @Field("email")
    private String email;
    
    @Field("phone")
    private String phone;
    
    @Field("license_number")
    private String licenseNumber;
    
    @Field("years_of_experience")
    private Integer yearsOfExperience;
    
    @Field("specialty")
    private String specialty;
    
    @Field("hospital_ids")
    private List<String> hospitalIds; // References to Hospital documents
    
    @Field("fee")
    private Double fee;
    
    @Field("available_for_telemedicine")
    private Boolean availableForTelemedicine;

    @Field("status")
    private DoctorStatus status;
}