package com.medilink.authservice.service;

import com.medilink.authservice.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final TokenBlacklistRepository blacklistRepository;
    
    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM daily
    public void cleanupExpiredTokens() {
        // MongoDB TTL will automatically delete expired tokens
        System.out.println("Cleaning up expired tokens...");
    }
    
    public boolean isTokenBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }
}