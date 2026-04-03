package com.medilink.authservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    private final String message;
    private final Object details;
    
    // Constructor with message only
    public CustomException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "BAD_REQUEST";
        this.message = message;
        this.details = null;
    }
    
    // Constructor with message and status
    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = status.name();
        this.message = message;
        this.details = null;
    }
    
    // Constructor with message, status, and error code
    public CustomException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.details = null;
    }
    
    // Constructor with message, status, error code, and details
    public CustomException(String message, HttpStatus status, String errorCode, Object details) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }
    
    // Constructor with cause
    public CustomException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.errorCode = "INTERNAL_ERROR";
        this.message = message;
        this.details = null;
    }
    
    // Constructor with message, status, and cause
    public CustomException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errorCode = status.name();
        this.message = message;
        this.details = null;
    }
    
    // Static factory methods for common exceptions
    public static CustomException notFound(String entityName, String id) {
        return new CustomException(
            String.format("%s not found with id: %s", entityName, id),
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND"
        );
    }
    
    public static CustomException alreadyExists(String entityName, String field, String value) {
        return new CustomException(
            String.format("%s already exists with %s: %s", entityName, field, value),
            HttpStatus.CONFLICT,
            "RESOURCE_ALREADY_EXISTS"
        );
    }
    
    public static CustomException unauthorized(String message) {
        return new CustomException(
            message,
            HttpStatus.UNAUTHORIZED,
            "UNAUTHORIZED_ACCESS"
        );
    }
    
    public static CustomException forbidden(String message) {
        return new CustomException(
            message,
            HttpStatus.FORBIDDEN,
            "ACCESS_DENIED"
        );
    }
    
    public static CustomException badRequest(String message) {
        return new CustomException(
            message,
            HttpStatus.BAD_REQUEST,
            "BAD_REQUEST"
        );
    }
    
    public static CustomException validationError(String message, Object details) {
        return new CustomException(
            message,
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            details
        );
    }
    
    public static CustomException tokenExpired() {
        return new CustomException(
            "JWT token has expired",
            HttpStatus.UNAUTHORIZED,
            "TOKEN_EXPIRED"
        );
    }
    
    public static CustomException invalidToken() {
        return new CustomException(
            "Invalid JWT token",
            HttpStatus.UNAUTHORIZED,
            "INVALID_TOKEN"
        );
    }
    
    public static CustomException userNotFound(String email) {
        return new CustomException(
            String.format("User not found with email: %s", email),
            HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND"
        );
    }
    
    public static CustomException userNotApproved() {
        return new CustomException(
            "Account not approved by admin. Please wait for approval.",
            HttpStatus.FORBIDDEN,
            "ACCOUNT_NOT_APPROVED"
        );
    }
    
    public static CustomException userDisabled() {
        return new CustomException(
            "Account is disabled. Contact administrator.",
            HttpStatus.FORBIDDEN,
            "ACCOUNT_DISABLED"
        );
    }
    
    public static CustomException invalidCredentials() {
        return new CustomException(
            "Invalid email or password",
            HttpStatus.UNAUTHORIZED,
            "INVALID_CREDENTIALS"
        );
    }
    
    public static CustomException emailAlreadyExists(String email) {
        return new CustomException(
            String.format("Email %s is already registered", email),
            HttpStatus.CONFLICT,
            "EMAIL_ALREADY_EXISTS"
        );
    }
    
    public static CustomException roleNotAllowed(String role) {
        return new CustomException(
            String.format("Role %s is not allowed for registration", role),
            HttpStatus.BAD_REQUEST,
            "INVALID_ROLE"
        );
    }
    
    public static CustomException databaseError(String operation, Throwable cause) {
        return new CustomException(
            String.format("Database error during %s", operation),
            HttpStatus.INTERNAL_SERVER_ERROR,
            "DATABASE_ERROR",
            cause
        );
    }
    
    public static CustomException serviceUnavailable(String serviceName) {
        return new CustomException(
            String.format("%s service is currently unavailable", serviceName),
            HttpStatus.SERVICE_UNAVAILABLE,
            "SERVICE_UNAVAILABLE"
        );
    }
    
    public static CustomException rateLimitExceeded() {
        return new CustomException(
            "Too many requests. Please try again later.",
            HttpStatus.TOO_MANY_REQUESTS,
            "RATE_LIMIT_EXCEEDED"
        );
    }
}