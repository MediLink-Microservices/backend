package com.medilink.authservice.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String userId;
    private String email;
    private String name;
    private String role;
    private boolean isApproved;
    private long expiresIn;
}