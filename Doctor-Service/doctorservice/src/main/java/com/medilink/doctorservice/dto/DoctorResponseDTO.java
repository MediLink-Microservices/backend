package com.medilink.doctorservice.dto;

import com.medilink.doctorservice.entity.DoctorStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class DoctorResponseDTO {
    private String doctorId;
    private String name;
    private String email;
    private String specialty;
    private String hospital;
    private Double fee;
    private DoctorStatus status;
}