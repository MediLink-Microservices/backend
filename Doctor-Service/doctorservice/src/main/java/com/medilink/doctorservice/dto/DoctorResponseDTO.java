package com.medilink.doctorservice.dto;

import com.medilink.doctorservice.entity.DoctorStatus;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class DoctorResponseDTO {
    private String doctorId;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private String specialty;
    private List<String> workLocations;
    private Double fee;
    private Boolean availableForTelemedicine;
    private DoctorStatus status;
}