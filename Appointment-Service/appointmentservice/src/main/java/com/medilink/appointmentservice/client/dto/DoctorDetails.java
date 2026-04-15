package com.medilink.appointmentservice.client.dto;

import java.util.List;
import lombok.Data;

@Data
public class DoctorDetails {
    private String doctorId;
    private String name;
    private String specialty;
    private List<String> hospitalIds;
    private Double fee;
}
