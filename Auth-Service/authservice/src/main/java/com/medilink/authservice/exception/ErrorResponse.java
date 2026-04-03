package com.medilink.authservice.exception;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String errorCode;
    private Object details;
    
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errorCode = "UNKNOWN_ERROR";
        this.details = null;
    }
    
    public ErrorResponse(int status, String message, LocalDateTime timestamp, String errorCode) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errorCode = errorCode;
        this.details = null;
    }
}