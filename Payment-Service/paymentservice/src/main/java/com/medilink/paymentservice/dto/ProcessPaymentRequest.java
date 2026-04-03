package com.medilink.paymentservice.dto;

import com.medilink.paymentservice.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {

    @NotBlank
    private String appointmentId;

    @NotBlank
    private String patientId;

    @DecimalMin("0.01")
    private double amount;

    @NotNull
    private PaymentMethod paymentMethod;

    private boolean simulateSuccess = true;
}
