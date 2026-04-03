package com.medilink.doctorservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "schedules")
public class Schedule {

    @Id
    private String scheduleId;

    @Field("doctor_id")
    private String doctorId;
    
    @Field("day")
    private String day;
    
    @Field("start_time")
    private String startTime;
    
    @Field("end_time")
    private String endTime;
    
    @Field("location")
    private String location;
    
    @Field("consultation_type")
    private String consultationType; // IN_PERSON, ONLINE, BOTH
    
    @Field("is_available")
    private Boolean isAvailable;
    
    @Field("patient_limit")
    private Integer patientLimit;
}
