package com.medilink.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medilink.authservice.dto.ApiResponse;
import com.medilink.authservice.dto.AuthResponse;
import com.medilink.authservice.dto.LoginRequest;
import com.medilink.authservice.dto.RegisterRequest;
import com.medilink.authservice.dto.ValidateTokenResponse;
import com.medilink.authservice.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        ApiResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // Admin-only: register any user with auto-approval (no manual approval needed)
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<ApiResponse> adminRegister(@Valid @RequestBody RegisterRequest request) {
        ApiResponse response = authService.adminRegister(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validateToken(@RequestHeader("Authorization") String token) {
        ValidateTokenResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        ApiResponse response = authService.logout(token);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestParam String refreshToken) {
        ApiResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats")
    public ResponseEntity<ApiResponse> getAdminStats() {
        System.out.println("DEBUG: AuthController reached - getAdminStats");
        return ResponseEntity.ok(authService.getAdminStats());
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<ApiResponse> getUsersByRole(@RequestParam(required = false, defaultValue = "ALL") String role) {
        return ResponseEntity.ok(authService.getUsersByRole(role));
    }
}