package com.medilink.doctorservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    
    @Field("specialty")
    private String specialty;
    
    @Field("hospital")
    private String hospital;
    
    @Field("fee")
    private Double fee;

    @Field("status")
    private DoctorStatus status;
}