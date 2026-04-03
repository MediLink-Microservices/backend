package com.medilink.authservice.security;

public class SecurityConstants {
    public static final String[] PUBLIC_URLS = {
        "/auth/register",
        "/auth/login",
        "/auth/validate",
        "/actuator/health",
        "/actuator/info"
    };
    
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}