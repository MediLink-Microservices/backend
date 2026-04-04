package com.medilink.telemedicine_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Telemedicine Service.
 * This service handles dynamic Jitsi Meet URL generation and appointment validation.
 */
@SpringBootApplication
public class TelemedicineServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemedicineServiceApplication.class, args);
    }
}
