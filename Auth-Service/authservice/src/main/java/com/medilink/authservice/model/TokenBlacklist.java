package com.medilink.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "token_blacklist")
public class TokenBlacklist {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    @Indexed(expireAfterSeconds = 86400) // Auto delete after 24 hours
    private LocalDateTime expiryDate;
    
    private String userId;
    private LocalDateTime blacklistedAt;
}