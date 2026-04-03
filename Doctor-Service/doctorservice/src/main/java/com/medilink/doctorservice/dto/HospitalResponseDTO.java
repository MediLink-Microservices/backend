package com.medilink.doctorservice.dto;

import com.medilink.doctorservice.entity.HospitalType;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class HospitalResponseDTO {
    
    private String hospitalId;
    private String name;
    private String address;
    private String city;
    private String province;
    private String telephone;
    private String email;
    private HospitalType type;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
