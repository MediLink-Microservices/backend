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
@Document(collection = "hospitals")
public class Hospital {

    @Id
    private String hospitalId;

    @Field("name")
    private String name;

    @Field("address")
    private String address;

    @Field("city")
    private String city;

    @Field("province")
    private String province;

    @Field("telephone")
    private String telephone;

    @Field("email")
    private String email;

    @Field("type")
    private HospitalType type;

    @Field("is_active")
    private Boolean isActive;

    @Field("created_at")
    private LocalDateTime createdAt;
}
