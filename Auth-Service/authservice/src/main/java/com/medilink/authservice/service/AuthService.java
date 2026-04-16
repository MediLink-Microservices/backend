package com.medilink.authservice.service;


import com.medilink.authservice.dto.*;
import com.medilink.authservice.exception.CustomException;
import com.medilink.authservice.model.TokenBlacklist;
import com.medilink.authservice.model.User;
import com.medilink.authservice.repository.TokenBlacklistRepository;
import com.medilink.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final TokenBlacklistRepository blacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public ApiResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw CustomException.emailAlreadyExists(request.getEmail());
        }
        
        // Validate role
        if (!request.getRole().matches("PATIENT|DOCTOR|ADMIN")) {
            throw CustomException.roleNotAllowed(request.getRole());
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setPhoneNumber(request.getPhoneNumber());
        
        // Auto-approve patients, doctors need admin approval
        if ("PATIENT".equals(request.getRole())) {
            user.setIsApproved(true);
        } else {
            user.setIsApproved(false);
        }
        
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        try {
            User savedUser = userRepository.save(user);
            return new ApiResponse("User registered successfully", 
                Map.of("userId", savedUser.getId(), "role", savedUser.getRole(), "approved", savedUser.getIsApproved()));
        } catch (Exception e) {
            throw CustomException.databaseError("user registration", e);
        }
    }

    // Called by authenticated admins — always auto-approves regardless of role
    public ApiResponse adminRegister(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw CustomException.emailAlreadyExists(request.getEmail());
        }
        if (!request.getRole().matches("PATIENT|DOCTOR|ADMIN")) {
            throw CustomException.roleNotAllowed(request.getRole());
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIsApproved(true);   // Always approved when created by admin
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        try {
            User savedUser = userRepository.save(user);
            return new ApiResponse("User created and approved successfully",
                Map.of("userId", savedUser.getId(), "role", savedUser.getRole(), "approved", true));
        } catch (Exception e) {
            throw CustomException.databaseError("admin user registration", e);
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> CustomException.userNotFound(request.getEmail()));
            
            // Check if user is approved
            if (!user.getIsApproved()) {
                throw CustomException.userNotApproved();
            }
            
            // Check if user is active
            if (!user.getIsActive()) {
                throw CustomException.userDisabled();
            }
            
            // Generate tokens
            String token = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());
            String refreshToken = jwtService.generateRefreshToken(user.getEmail());
            
            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setRefreshToken(refreshToken);
            response.setUserId(user.getId());
            response.setEmail(user.getEmail());
            response.setName(user.getName());
            response.setRole(user.getRole());
            response.setApproved(user.getIsApproved());
            response.setExpiresIn(86400000L);
            
            return response;
            
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage().contains("Bad credentials")) {
                throw CustomException.invalidCredentials();
            }
            throw CustomException.badRequest("Login failed: " + e.getMessage());
        }
    }
    
    public ValidateTokenResponse validateToken(String token) {
        // Remove Bearer prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            boolean isValid = jwtService.validateToken(token);
            
            if (isValid) {
                String email = jwtService.getEmailFromToken(token);
                String role = jwtService.getRoleFromToken(token);
                String userId = jwtService.getUserIdFromToken(token);
                
                return new ValidateTokenResponse(true, email, role, userId);
            } else {
                throw CustomException.invalidToken();
            }
        } catch (Exception e) {
            if (e.getMessage().contains("expired")) {
                throw CustomException.tokenExpired();
            }
            throw CustomException.invalidToken();
        }
    }
    
    public ApiResponse logout(String token) {
        // Remove Bearer prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            // Add token to blacklist
            TokenBlacklist blacklistedToken = new TokenBlacklist();
            blacklistedToken.setToken(token);
            blacklistedToken.setExpiryDate(jwtService.getExpirationDateFromToken(token).toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
            blacklistedToken.setBlacklistedAt(LocalDateTime.now());
            
            String userId = jwtService.getUserIdFromToken(token);
            blacklistedToken.setUserId(userId);
            
            blacklistRepository.save(blacklistedToken);
            
            return new ApiResponse("Logged out successfully", null);
        } catch (Exception e) {
            throw CustomException.badRequest("Logout failed: " + e.getMessage());
        }
    }
    
    public ApiResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw CustomException.invalidToken();
        }
        
        String email = jwtService.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> CustomException.userNotFound(email));
        
        String newToken = jwtService.generateToken(user.getEmail(), user.getRole(), user.getId());
        
        return new ApiResponse("Token refreshed successfully", Map.of("token", newToken));
    }

    public ApiResponse getAdminStats() {
        long doctors = userRepository.countByRole("DOCTOR");
        long patients = userRepository.countByRole("PATIENT");
        long admins = userRepository.countByRole("ADMIN");
        long pending = userRepository.countByRoleAndIsApprovedFalse("DOCTOR");
        
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("doctors", doctors);
        stats.put("patients", patients);
        stats.put("admins", admins);
        stats.put("pending", pending);
        
        return new ApiResponse("Stats fetched", stats);
    }

    public ApiResponse getUsersByRole(String role) {
        java.util.List<User> users;
        if ("ALL".equals(role)) {
            users = userRepository.findAll();
        } else {
            users = userRepository.findByRole(role);
        }
        
        java.util.List<Map<String, Object>> safeUsers = users.stream().map(u -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole());
            map.put("isApproved", u.getIsApproved());
            map.put("createdAt", u.getCreatedAt());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        
        return new ApiResponse("Users fetched", Map.of("users", safeUsers));
    }
}