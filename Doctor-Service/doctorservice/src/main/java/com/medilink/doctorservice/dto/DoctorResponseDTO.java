package com.healthcare.doctor_service.dto;

import com.healthcare.doctor_service.entity.DoctorStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DoctorResponseDTO {
    private Long doctorId;
    private String name;
    private String email;
    private String specialty;
    private String hospital;
    private Double consultationFee;
    private DoctorStatus status;
}