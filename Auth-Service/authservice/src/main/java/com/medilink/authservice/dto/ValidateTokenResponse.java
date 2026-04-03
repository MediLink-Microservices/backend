package com.medilink.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateTokenResponse {
    private boolean valid;
    private String email;
    private String role;
    private String userId;
}