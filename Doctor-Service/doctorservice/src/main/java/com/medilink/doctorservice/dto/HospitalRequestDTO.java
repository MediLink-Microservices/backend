package com.medilink.doctorservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class HospitalRequestDTO {
    
    @NotBlank(message = "Hospital name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @NotBlank(message = "Province is required")
    @Size(max = 50, message = "Province must not exceed 50 characters")
    private String province;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
    private String telephone;

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Hospital type is required")
    private com.medilink.doctorservice.entity.HospitalType type;

    private Boolean isActive;
}
