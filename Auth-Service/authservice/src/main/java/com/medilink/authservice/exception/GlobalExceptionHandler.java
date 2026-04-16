package com.medilink.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        System.err.println("GLOBAL EXCEPTION CAUGHT: " + ex.getClass().getName() + ": " + ex.getMessage());
        ex.printStackTrace();
        
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Internal Server Error: " + ex.getMessage());
        body.put("type", ex.getClass().getSimpleName());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex) {
        System.err.println("CUSTOM EXCEPTION CAUGHT: " + ex.getMessage() + " Status: " + ex.getStatus());
        
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("errorCode", ex.getErrorCode());
        
        return new ResponseEntity<>(body, ex.getStatus());
    }
}