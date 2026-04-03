package com.medilink.authservice.repository;

import com.medilink.authservice.model.TokenBlacklist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends MongoRepository<TokenBlacklist, String> {
    boolean existsByToken(String token);
}